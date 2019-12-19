package handler;
import java.io.IOException;
import javax.mail.MessagingException;

import com.amazonaws.services.s3.model.CopyObjectRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.fasterxml.jackson.databind.ObjectMapper;
import parser.MailParser;
import parser.MailParserFactory;
import parser.PscDiscrepancySurveyCsvProcessor;
import parser.PscDiscrepancySurveyCsvProcessorFactory;
import service.AmazonS3Service;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.environment.impl.EnvironmentReaderImpl;

public class Handler implements RequestHandler<S3Event, String> {
    private static final String SOURCE_FOLDER_PREFIX = "source/";
    private static final String REJECTED_FOLDER_PREFIX = "rejected/";
    private static final String ACCEPTED_FOLDER_PREFIX = "accepted/";
    private static final String CHIPS_REST_INTERFACE_ENDPOINT = "CHIPS_REST_INTERFACE_ENDPOINT";
    private static final Logger LOG = LogManager.getLogger(Handler.class);
    private final AmazonS3Service amazonS3Service;
    private final EnvironmentReader environmentReader;
    private final MailParserFactory mailParserFactory;
    private final PscDiscrepancySurveyCsvProcessorFactory csvParserFactory;

    protected Handler(AmazonS3Service amazonS3Service, EnvironmentReader environmentReader,
            MailParserFactory mailParserFactory,
            PscDiscrepancySurveyCsvProcessorFactory csvParserFactory) {
        this.amazonS3Service = amazonS3Service;
        this.environmentReader = environmentReader;
        this.mailParserFactory = mailParserFactory;
        this.csvParserFactory = csvParserFactory;
    }

    public Handler() {
        this(new AmazonS3Service(), new EnvironmentReaderImpl(), new MailParserFactory(),
                new PscDiscrepancySurveyCsvProcessorFactory());
    }

    @Override
    public String handleRequest(S3Event s3event, Context context) {
        String chipsEnvUri = environmentReader.getMandatoryString(CHIPS_REST_INTERFACE_ENDPOINT);
        String requestId = context.getAwsRequestId();
        LOG.info("handleRequest entry for awsRequestId: {}", requestId);
        for (S3EventNotificationRecord record : s3event.getRecords()) {
            String s3Key = amazonS3Service.getKey(record);
            String s3Bucket = amazonS3Service.getBucket(record);
            S3Object s3Object = amazonS3Service.getFileFromS3(s3Bucket, s3Key);
            LOG.info("handleRequest for S3 for s3Key: [{}], s3Bucket: [{}], s3Object: [{}]", s3Key,
                    s3Bucket, s3Object);
            S3ObjectInputStream in = s3Object.getObjectContent();
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                MailParser mailParser = mailParserFactory.createMailParser(in);
                byte[] extractedCsv = mailParser.extractCsvAttachment();
                LOG.info("Parsed email");
                PscDiscrepancyFoundListenerImpl listener =
                        new PscDiscrepancyFoundListenerImpl(httpClient, chipsEnvUri,
                                new ObjectMapper(), requestId);
                PscDiscrepancySurveyCsvProcessor csvParser = csvParserFactory
                        .createPscDiscrepancySurveyCsvProcessor(extractedCsv, listener);
                LOG.info("About to parse CSV");
                if (csvParser.parseRecords()) {
                    LOG.info("Successfully processed CSV");
                    putFile(s3Bucket, s3Key, ACCEPTED_FOLDER_PREFIX);
                } else {
                    putFile(s3Bucket, s3Key, REJECTED_FOLDER_PREFIX);
                }
                LOG.info("Finished processing CSV");
            } catch (MessagingException me) {
                LOG.error("Email: " + s3Key
                        + " is corrupt or missing attachment - moving to rejected folder", me);
                putFile(s3Bucket, s3Key, REJECTED_FOLDER_PREFIX);
            } catch (IOException e) {
                LOG.error("The attachment in the email: " + s3Key
                        + " is not found - moving to the rejected folder");
                putFile(s3Bucket, s3Key, REJECTED_FOLDER_PREFIX);
            }
        }
        LOG.info("handleRequest exit");
        return "ok";
    }

    private void putFile(String s3Bucket, String s3Key, String folder) {
        String changedS3Key = s3Key.replace(SOURCE_FOLDER_PREFIX, folder);
        CopyObjectRequest copyObjectRequest =
                new CopyObjectRequest(s3Bucket, s3Key, s3Bucket, changedS3Key);
        amazonS3Service.moveFileInS3(copyObjectRequest);
    }
}
