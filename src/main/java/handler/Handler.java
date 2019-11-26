package handler;

import java.io.IOException;
import javax.mail.MessagingException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.fasterxml.jackson.databind.ObjectMapper;
import parser.PscDiscrepancySurveyCsvProcessor;
import parser.MailParser;
import service.AmazonS3Service;

public class Handler implements RequestHandler<S3Event, String> {

    private static final String SOURCE_FOLDER_PREFIX = "source/";
    private static final String REJECTED_FOLDER_PREFIX = "rejected/";
    private static final String ACCEPTED_FOLDER_PREFIX = "accepted/";
    private static final Logger LOG = LogManager.getLogger(Handler.class);

    private final AmazonS3Service amazonS3Service = new AmazonS3Service();

    public String handleRequest(S3Event s3event, Context context) {
        LOG.info("handleRequest entry");
        for (S3EventNotificationRecord record : s3event.getRecords()) {
            String s3Key = amazonS3Service.getKey(record);
            String s3Bucket = amazonS3Service.getBucket(record);
            S3Object s3Object = amazonS3Service.getFileFromS3(s3Bucket, s3Key);
            LOG.info("handleRequest for S3 for s3Key: [{}], s3Bucket: [{}], s3Object: [{}]", s3Key, s3Bucket, s3Object);
            S3ObjectInputStream in = s3Object.getObjectContent();

            try {
                MailParser mailParser = new MailParser(in);
                byte[] extractedCsv = mailParser.extractCsvAttachment();
                LOG.error("Parsed email");
                
                try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                    PscDiscrepancyFoundListenerImpl listener = new PscDiscrepancyFoundListenerImpl(httpClient, "http://chpdev-pl6.internal.ch:21011/chips-restService/rest/chipsgeneric/pscDiscrepancies", new ObjectMapper());
                    PscDiscrepancySurveyCsvProcessor csvParser = new PscDiscrepancySurveyCsvProcessor(extractedCsv, listener);
                    LOG.error("About to parse CSV");
                    boolean isParsed = csvParser.parseRecords();
                    moveProcessedFile(s3Bucket, s3Key, in, isParsed);
                    LOG.error("Finished processing CSV");
                }
            } catch (MessagingException me) {
                LOG.error("Email: " + s3Key + " is corrupt or missing attachment - moving to rejected folder", me);
                String changedS3key = s3Key.replace(SOURCE_FOLDER_PREFIX, REJECTED_FOLDER_PREFIX);
                amazonS3Service.putFileInS3(s3Bucket, changedS3key, in, new ObjectMetadata());
            } catch (IOException e) {
                LOG.error("The attachment in the email: " + s3Key
                                + " is not found - moving to the rejected folder");
                String changedS3key = s3Key.replace(SOURCE_FOLDER_PREFIX, REJECTED_FOLDER_PREFIX);
                amazonS3Service.putFileInS3(s3Bucket, changedS3key , in, new ObjectMetadata());
            }
        }
        LOG.info("handleRequest exit");
        return "ok";
    }

    private void moveProcessedFile(String s3Bucket, String s3Key, S3ObjectInputStream in,
            boolean isParsed) {
        if (isParsed) {
            String changedS3Key = s3Key.replace(SOURCE_FOLDER_PREFIX, ACCEPTED_FOLDER_PREFIX);
            amazonS3Service.putFileInS3(s3Bucket, changedS3Key, in, new ObjectMetadata());
        } else {
            String changedS3Key = s3Key.replace(SOURCE_FOLDER_PREFIX, REJECTED_FOLDER_PREFIX);
            amazonS3Service.putFileInS3(s3Bucket, changedS3Key, in, new ObjectMetadata());
        }
    }
}

