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
import parser.CsvExtractor;
import parser.CsvExtractorFactory;
import parser.CsvProcessor;
import parser.CsvProcessorFactory;
import service.AmazonS3Service;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.environment.impl.EnvironmentReaderImpl;

/**
 * Entry point for Lambda. The AWS infrastructure constructs this using the no-arg constructor and
 * invokes {@link #handleRequest(S3Event, Context)}.
 */
public class Handler implements RequestHandler<S3Event, String> {
    private static final String SOURCE_FOLDER_PREFIX = "source/";
    private static final String REJECTED_FOLDER_PREFIX = "rejected/";
    private static final String ACCEPTED_FOLDER_PREFIX = "accepted/";
    private static final String CHIPS_REST_INTERFACE_ENDPOINT = "CHIPS_REST_INTERFACE_ENDPOINT";
    private static final Logger LOG = LogManager.getLogger(Handler.class);
    private final AmazonS3Service amazonS3Service;
    private final EnvironmentReader environmentReader;
    private final CsvExtractorFactory csvExtractorFactory;
    private final CsvProcessorFactory csvParserFactory;

    protected Handler(AmazonS3Service amazonS3Service, EnvironmentReader environmentReader,
            CsvExtractorFactory csvExtractorFactory,
            CsvProcessorFactory csvParserFactory) {
        this.amazonS3Service = amazonS3Service;
        this.environmentReader = environmentReader;
        this.csvExtractorFactory = csvExtractorFactory;
        this.csvParserFactory = csvParserFactory;
    }

    public Handler() {
        this(new AmazonS3Service(), new EnvironmentReaderImpl(), new CsvExtractorFactory(),
                new CsvProcessorFactory());
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
                CsvExtractor csvExtractor = csvExtractorFactory.createMailParser(in);
                byte[] extractedCsv = csvExtractor.extractCsvAttachment();
                LOG.info("Parsed email");

                PscDiscrepancySurveySender listener =
                        new PscDiscrepancySurveySender(httpClient, chipsEnvUri,
                                new ObjectMapper(), requestId);
                CsvProcessor csvParser = csvParserFactory
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
                LOG.error("The attachment in the email: {} is not found - moving to the rejected folder", s3Key);
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
