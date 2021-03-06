package handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.mail.MessagingException;

import com.amazonaws.services.s3.model.CopyObjectRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import parser.CsvExtractor;
import parser.CsvExtractorFactory;
import parser.CsvProcessor;
import parser.CsvProcessorFactory;
import service.AmazonS3Service;
import uk.gov.companieshouse.environment.EnvironmentReader;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class HandlerTest {
    private static final String BUCKET_NAME = "bucketName";

    private static final String ACCEPTED_FILE_NAME = "accepted/fileName.csv";
    private static final String SOURCE_FILE_NAME = "source/fileName.csv";
    private static final String REJECTED_FILE_NAME = "rejected/fileName.csv";
    private byte[] extractedCsv;

    private final List<S3EventNotificationRecord> records = new ArrayList<>();

    @Captor
    private ArgumentCaptor<CopyObjectRequest> argCaptor;

    @Mock
    private S3Event s3Event;
    @Mock
    private CsvExtractorFactory csvExtractorFactory;
    @Mock
    private CsvProcessorFactory csvParserFactory;
    @Mock
    private S3EventNotificationRecord record;
    @Mock
    private AmazonS3Service amazonS3Service;
    @Mock
    private EnvironmentReader environmentReader;
    @Mock
    private Context context;
    @Mock
    private S3Object s3Object;
    @Mock
    private CsvProcessor csvParser;
    @Mock
    private S3ObjectInputStream s3ObjectInputStream;
    @Mock
    private CsvExtractor csvExtractor;

    @InjectMocks
    private Handler handler;

    @BeforeEach
    void setUp() throws MessagingException, IOException {
        records.add(record);
        when(s3Event.getRecords()).thenReturn(records);
        when(amazonS3Service.getKey(record)).thenReturn(SOURCE_FILE_NAME);
        when(amazonS3Service.getBucket(record)).thenReturn(BUCKET_NAME);
        when(amazonS3Service.getFileFromS3(BUCKET_NAME, SOURCE_FILE_NAME)).thenReturn(s3Object);
        when(s3Object.getObjectContent()).thenReturn(s3ObjectInputStream);
        when(csvExtractorFactory.createMailParser(s3ObjectInputStream)).thenReturn(csvExtractor);
        when(csvExtractor.extractCsvAttachment()).thenReturn(extractedCsv);
    }

    @Test
    @DisplayName("Successful upload of valid CSV file into accepted folder")
    void uploadValidCSV_Successful() throws IOException, MessagingException {
        when(csvParserFactory.createPscDiscrepancySurveyCsvProcessor(any(),
                        any(PscDiscrepancySurveySender.class))).thenReturn(csvParser);
        when(csvParser.parseRecords()).thenReturn(true);
        String result = handler.handleRequest(s3Event, context);
        verify(amazonS3Service).moveFileInS3(argCaptor.capture());
        assertEquals(BUCKET_NAME, argCaptor.getValue().getSourceBucketName());
        assertEquals(SOURCE_FILE_NAME, argCaptor.getValue().getSourceKey());
        assertEquals(BUCKET_NAME, argCaptor.getValue().getDestinationBucketName());
        assertEquals(ACCEPTED_FILE_NAME, argCaptor.getValue().getDestinationKey());
        assertEquals("ok", result);
    }

    @Test
    @DisplayName("Successful upload of invalid CSV file into rejected folder")
    void uploadInvalidCSV_Successful() throws IOException {
        when(csvParserFactory.createPscDiscrepancySurveyCsvProcessor(any(),
                        any(PscDiscrepancySurveySender.class))).thenReturn(csvParser);
        when(csvParser.parseRecords()).thenReturn(false);
        String result = handler.handleRequest(s3Event, context);
        verify(amazonS3Service).moveFileInS3(argCaptor.capture());
        assertEquals(BUCKET_NAME, argCaptor.getValue().getSourceBucketName());
        assertEquals(SOURCE_FILE_NAME, argCaptor.getValue().getSourceKey());
        assertEquals(BUCKET_NAME, argCaptor.getValue().getDestinationBucketName());
        assertEquals(REJECTED_FILE_NAME, argCaptor.getValue().getDestinationKey());
        assertEquals("ok", result);
    }

    @Test
    @DisplayName("When attempting to extract csv attachment throw messaging exception")
    void throwMessagingExceptionWhenParsingMail() throws IOException, MessagingException {
        when(csvExtractor.extractCsvAttachment()).thenThrow(new MessagingException());

        String result = handler.handleRequest(s3Event, context);
        verify(amazonS3Service).moveFileInS3(argCaptor.capture());
        assertEquals(BUCKET_NAME, argCaptor.getValue().getSourceBucketName());
        assertEquals(SOURCE_FILE_NAME, argCaptor.getValue().getSourceKey());
        assertEquals(BUCKET_NAME, argCaptor.getValue().getDestinationBucketName());
        assertEquals(REJECTED_FILE_NAME, argCaptor.getValue().getDestinationKey());
        assertEquals("ok", result);
    }

    @Test
    @DisplayName("When attempting to extract csv attachment throw messaging exception")
    void throwIOExceptionWhenParsingMail() throws IOException, MessagingException {
        when(csvExtractor.extractCsvAttachment()).thenThrow(new IOException());

        String result = handler.handleRequest(s3Event, context);
        verify(amazonS3Service).moveFileInS3(argCaptor.capture());
        assertEquals(BUCKET_NAME, argCaptor.getValue().getSourceBucketName());
        assertEquals(SOURCE_FILE_NAME, argCaptor.getValue().getSourceKey());
        assertEquals(BUCKET_NAME, argCaptor.getValue().getDestinationBucketName());
        assertEquals(REJECTED_FILE_NAME, argCaptor.getValue().getDestinationKey());
        assertEquals("ok", result);
    }
}
