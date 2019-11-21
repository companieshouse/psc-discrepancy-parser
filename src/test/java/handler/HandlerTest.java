//package handler;
//
//import com.amazonaws.services.lambda.runtime.Context;
//import com.amazonaws.services.lambda.runtime.LambdaLogger;
//import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
//import com.amazonaws.services.s3.model.ObjectMetadata;
//import com.amazonaws.services.lambda.runtime.events.S3Event;
//import com.amazonaws.services.s3.model.S3Object;
//import com.amazonaws.services.s3.model.S3ObjectInputStream;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Captor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import service.AmazonS3Service;
//import service.DiscrepancyService;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//public class HandlerTest {
//    private static final String BUCKET_NAME = "bucketName";
//
//    private static final String ACCEPTED_FILE_NAME = "accepted/fileName.csv";
//    private static final String SOURCE_FILE_NAME = "source/fileName.csv";
//    private static final String REJECTED_FILE_NAME = "rejected/fileName.csv";
//
//    List<S3EventNotificationRecord> records = new ArrayList<>();
//
//    @Captor
//    ArgumentCaptor argCaptor;
//
//    @Mock
//    S3Event s3Event;
//
//    @Mock
//    S3EventNotificationRecord record;
//
//    @Mock
//    AmazonS3Service amazonS3Service;
//
//    @Mock
//    DiscrepancyService discrepancyService;
//
//    @Mock
//    Context context;
//
//    @Mock
//    LambdaLogger lambdaLogger;
//
//    @Mock
//    S3Object s3Object;
//
//    @Mock
//    S3ObjectInputStream s3ObjectInputStream;
//
//    @InjectMocks
//    Handler handler;
//
//    @BeforeEach
//    void setUp() {
//        records.add(record);
//        when(s3Event.getRecords()).thenReturn(records);
//        when(amazonS3Service.getKey(record)).thenReturn(SOURCE_FILE_NAME);
//        when(amazonS3Service.getBucket(record)).thenReturn(BUCKET_NAME);
//        when(amazonS3Service.getFileFromS3(BUCKET_NAME, SOURCE_FILE_NAME)).thenReturn(s3Object);
//        when(context.getLogger()).thenReturn(lambdaLogger);
//        when(s3Object.getObjectContent()).thenReturn(s3ObjectInputStream);
//    }
//
//    @Test
//    @DisplayName("Successful upload of valid CSV file into accepted folder")
//    void uploadValidCSV_Successful() {
////        when(discrepancyService.validateCsv()).thenReturn(true);
////        handler.handleRequest(s3Event, context);
////        verify(amazonS3Service).putFileInS3(anyString(), (String) argCaptor.capture(), any(S3ObjectInputStream.class), any(ObjectMetadata.class));
////
////        assertEquals(ACCEPTED_FILE_NAME, argCaptor.getValue());
//    }
//
//    @Test
//    @DisplayName("Successful upload of invalid CSV file into rejected folder")
//    void uploadInvalidCSV_Successful() {
////        when(discrepancyService.validateCsv()).thenReturn(false);
////        handler.handleRequest(s3Event, context);
////        verify(amazonS3Service).putFileInS3(anyString(), (String) argCaptor.capture(), any(S3ObjectInputStream.class), any(ObjectMetadata.class));
////
////        assertEquals(REJECTED_FILE_NAME, argCaptor.getValue());
//    }
//}
