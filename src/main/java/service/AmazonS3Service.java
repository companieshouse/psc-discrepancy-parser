package service;

import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import provider.AmazonS3Provider;

public class AmazonS3Service {
    private AmazonS3Provider amazonS3Provider = new AmazonS3Provider();

    public String getKey(S3EventNotificationRecord record) {
        return record.getS3().getObject().getKey();
    }

    public String getBucket(S3EventNotificationRecord record) {
        return record.getS3().getBucket().getName();
    }

    public S3Object getFileFromS3(String s3Bucket, String s3Key) {
        return amazonS3Provider.provide().getObject(s3Bucket, s3Key);
    }

    public void moveFileInS3(CopyObjectRequest copyObjectRequest) {
        amazonS3Provider.provide().copyObject(copyObjectRequest);
    }

}
