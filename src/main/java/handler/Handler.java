package handler;

import java.io.IOException;
import javax.mail.MessagingException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import parser.CsvParser;
import parser.MailParser;
import service.AmazonS3Service;
import service.DiscrepancyService;


public class Handler implements RequestHandler<S3Event, String> {

    private static final String PROCESSED_FOLDER_PREFIX = "processed/";
    private static final String SOURCE_FOLDER_PREFIX = "source/";
    private static final String REJECTED_FOLDER_PREFIX = "rejected/";
    private static final String ACCEPTED_FOLDER_PREFIX = "accepted/";
    private static final String REST_URI = "http://google.com";

    private DiscrepancyService discrepancyService = new DiscrepancyService();
    private AmazonS3Service amazonS3Service = new AmazonS3Service();
    private MailParser mailParser;
    private CsvParser csvParser;
    private PscDiscrepancyFoundListenerImpl listener;
    
    

    public String handleRequest(S3Event s3event, Context context) {

        byte[] extractedCsv = new byte[] {};

        for (S3EventNotificationRecord record : s3event.getRecords()) {
            String s3Key = amazonS3Service.getKey(record);
            String s3Bucket = amazonS3Service.getBucket(record);
            context.getLogger().log("found id: " + s3Bucket + " " + s3Key);
            S3Object s3Object = amazonS3Service.getFileFromS3(s3Bucket, s3Key);
            S3ObjectInputStream in = s3Object.getObjectContent();

            try {
                mailParser = new MailParser(in);
                extractedCsv = mailParser.extractCsvAttachment();
                listener = new PscDiscrepancyFoundListenerImpl(context.getLogger());
                csvParser = new CsvParser(extractedCsv, listener);
                
            } catch (MessagingException me) {
                context.getLogger()
                                .log("Email: " + s3Key + " is corrupt - moving to rejected folder");
                s3Key.replace(SOURCE_FOLDER_PREFIX, REJECTED_FOLDER_PREFIX);
                amazonS3Service.putFileInS3(s3Bucket, s3Key, in, new ObjectMetadata());
            } catch (IOException e) {
                context.getLogger().log("The attachment in the email: " + s3Key
                                + " is not found - moving to the rejected folder");
                s3Key.replace(SOURCE_FOLDER_PREFIX, REJECTED_FOLDER_PREFIX);
                amazonS3Service.putFileInS3(s3Bucket, s3Key, in, new ObjectMetadata());
            }

        }

        return "ok";

    }
}
