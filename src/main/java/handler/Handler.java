package handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import model.Discrepancy;
import service.AmazonS3Service;
import service.DiscrepancyService;

import java.util.ArrayList;
import java.util.List;


public class Handler implements RequestHandler<S3Event, String> {

    private static final String PROCESSED_FOLDER_PREFIX = "processed/";
    private static final String SOURCE_FOLDER_PREFIX = "source/";
    private static final String REJECTED_FOLDER_PREFIX = "rejected/";
    private static final String ACCEPTED_FOLDER_PREFIX = "accepted/";

    private DiscrepancyService discrepancyService = new DiscrepancyService();
    private AmazonS3Service amazonS3Service = new AmazonS3Service();

    public String handleRequest(S3Event s3event, Context context) {

        List<Discrepancy> discrepancies = new ArrayList<>();

        for (S3EventNotificationRecord record : s3event.getRecords()) {
            String s3Key = amazonS3Service.getKey(record);
            String s3Bucket = amazonS3Service.getBucket(record);
            context.getLogger().log("found id: " + s3Bucket + " " + s3Key);
            S3Object s3Object = amazonS3Service.getFileFromS3(s3Bucket, s3Key);
            S3ObjectInputStream in = s3Object.getObjectContent();

            if(discrepancyService.validateCsv()) {
                context.getLogger().log("CSV file is valid - uploading to processed folder");
                s3Key = s3Key.replace(SOURCE_FOLDER_PREFIX, ACCEPTED_FOLDER_PREFIX);
                amazonS3Service.putFileInS3(s3Bucket, s3Key, in, new ObjectMetadata());
                discrepancies = discrepancyService.readObjectContent(s3Object);
            } else {
                context.getLogger().log("CSV file is invalid - uploading to rejected folder");
                s3Key = s3Key.replace(SOURCE_FOLDER_PREFIX, REJECTED_FOLDER_PREFIX);
                amazonS3Service.putFileInS3(s3Bucket, s3Key, in, new ObjectMetadata());
            }

            discrepancies.forEach(d -> System.out.println(d.getCompanyName()));
        }


        return "ok";

    }
}
