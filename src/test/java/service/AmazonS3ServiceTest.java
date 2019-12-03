package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.event.S3EventNotification.S3BucketEntity;
import com.amazonaws.services.s3.event.S3EventNotification.S3Entity;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.event.S3EventNotification.S3ObjectEntity;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import provider.AmazonS3Provider;

@ExtendWith(MockitoExtension.class)
public class AmazonS3ServiceTest {
    private static final String KEY = "fileName";
    private static final String BUCKET = "bucketName";

    @Mock
    AmazonS3 s3Client;

    @Mock
    S3EventNotificationRecord record;

    @Mock
    S3Entity s3Entity;

    @Mock
    S3ObjectEntity s3ObjectEntity;

    @Mock
    S3BucketEntity s3BucketEntity;

    @Mock
    AmazonS3Provider amazonS3Provider;

    @Mock
    S3Object s3Object;

    @Mock
    S3ObjectInputStream s3ObjectInputStream;

    @Mock
    ObjectMetadata objectMetadata;

    @InjectMocks
    AmazonS3Service amazonS3Service;

    @Test
    @DisplayName("Successful retrieval of key of file in S3")
    void getKey_Successful() {
        when(record.getS3()).thenReturn(s3Entity);
        when(s3Entity.getObject()).thenReturn(s3ObjectEntity);
        when(s3ObjectEntity.getKey()).thenReturn(KEY);

        String key = amazonS3Service.getKey(record);
        assertEquals(KEY, key);
    }

    @Test
    @DisplayName("Successful retrieval of bucket of file in S3")
    void getBucket_Successful() {
        when(record.getS3()).thenReturn(s3Entity);
        when(s3Entity.getBucket()).thenReturn(s3BucketEntity);
        when(s3BucketEntity.getName()).thenReturn(BUCKET);

        String bucket = amazonS3Service.getBucket(record);
        assertEquals(BUCKET, bucket);
    }

    @Test
    @DisplayName("Successful retrieval of file in S3")
    void getFile_Successful() {
        when(amazonS3Provider.provide()).thenReturn(s3Client);
        when(s3Client.getObject(BUCKET, KEY)).thenReturn(s3Object);

        S3Object s3ObjectResult = amazonS3Service.getFileFromS3(BUCKET, KEY);
        assertEquals(s3Object, s3ObjectResult);
    }

    @Test
    @DisplayName("Successful upload of file in S3")
    void putFile_Successful() {
        when(amazonS3Provider.provide()).thenReturn(s3Client);
        amazonS3Service.putFileInS3(BUCKET, KEY, s3ObjectInputStream, objectMetadata);

        verify(s3Client).putObject(BUCKET, KEY, s3ObjectInputStream, objectMetadata);
    }
}
