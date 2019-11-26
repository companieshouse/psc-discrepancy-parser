package handler;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.PscDiscrepancySurvey;
import parser.CsvParser.PscDiscrepancyFoundListener;

@ExtendWith(MockitoExtension.class)
public class PscDiscrepancyFoundListenerImplTest {

    @Mock
    private CloseableHttpResponse response;
    @Mock
    private HttpEntity entity;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private StatusLine statusLine;
    @Mock
    private CloseableHttpClient client;

    private String REST_API = "http://test.ch:00000/chips";
    private PscDiscrepancySurvey discrepancy;

    private PscDiscrepancyFoundListener pscDiscrepancyFoundListenerImpl;

    @BeforeEach
    public void setUp() {
        pscDiscrepancyFoundListenerImpl =
                        new PscDiscrepancyFoundListenerImpl(client, REST_API, objectMapper);
        discrepancy = new PscDiscrepancySurvey();
    }

    @Test
    public void testJsonSentSuccessfully() throws ClientProtocolException, IOException {
        when(objectMapper.writeValueAsString(discrepancy)).thenReturn("");
        when(client.execute(any(HttpPost.class))).thenReturn(response);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_ACCEPTED);

        assertTrue(pscDiscrepancyFoundListenerImpl.parsed(discrepancy));
    }

    @Test
    public void testJsonSentUnsuccessfully() throws ClientProtocolException, IOException {
        when(objectMapper.writeValueAsString(discrepancy)).thenReturn("");
        when(client.execute(any(HttpPost.class))).thenReturn(response);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_BAD_GATEWAY);

        assertFalse(pscDiscrepancyFoundListenerImpl.parsed(discrepancy));
    }

    @Test
    public void testThrowsJsonProcessingException() throws ClientProtocolException, IOException {
        when(objectMapper.writeValueAsString(discrepancy))
                        .thenThrow(new JsonProcessingException("") {});

        assertFalse(pscDiscrepancyFoundListenerImpl.parsed(discrepancy));
    }

    @Test
    public void testThrowsIOException() throws ClientProtocolException, IOException {
        when(objectMapper.writeValueAsString(discrepancy)).thenReturn("");
        when(client.execute(any(HttpPost.class))).thenThrow(new IOException());

        assertFalse(pscDiscrepancyFoundListenerImpl.parsed(discrepancy));
    }
}
