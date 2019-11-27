package handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.PscDiscrepancySurvey;
import parser.PscDiscrepancySurveyCsvProcessor.PscDiscrepancyCreatedListener;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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
    @Captor
    private ArgumentCaptor<HttpPost> argCaptor;
    private static final String CORRELATION_ID = "1234";
    private static final String REST_API = "http://test.ch:00000/chips";
    private PscDiscrepancySurvey discrepancy;

    private PscDiscrepancyCreatedListener pscDiscrepancyFoundListenerImpl;

    @BeforeEach
    public void setUp() {
        pscDiscrepancyFoundListenerImpl =
                        new PscDiscrepancyFoundListenerImpl(client, REST_API, objectMapper, CORRELATION_ID);
        discrepancy = new PscDiscrepancySurvey();
    }

    @Test
    public void testJsonSentSuccessfully() throws ClientProtocolException, IOException {
        when(objectMapper.writeValueAsString(discrepancy)).thenReturn("jsonSuccessful");
        when(client.execute(any(HttpPost.class))).thenReturn(response);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_ACCEPTED);

        assertTrue(pscDiscrepancyFoundListenerImpl.created(discrepancy));

        verify(client).execute((HttpUriRequest) argCaptor.capture());

        HttpPost httpPost = (HttpPost) argCaptor.getValue();
        StringEntity stringEntity = (StringEntity) httpPost.getEntity();
        String result = IOUtils.toString(stringEntity.getContent());
        assertEquals("jsonSuccessful", result);

    }

    @Test
    public void testJsonSentUnsuccessfully() throws ClientProtocolException, IOException {
        when(objectMapper.writeValueAsString(discrepancy)).thenReturn("jsonUnsuccessful");
        when(client.execute(any(HttpPost.class))).thenReturn(response);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_BAD_GATEWAY);

        assertFalse(pscDiscrepancyFoundListenerImpl.created(discrepancy));

        verify(client).execute((HttpUriRequest) argCaptor.capture());

        HttpPost httpPost = (HttpPost) argCaptor.getValue();
        StringEntity stringEntity = (StringEntity) httpPost.getEntity();
        String result = IOUtils.toString(stringEntity.getContent());
        assertEquals("jsonUnsuccessful", result);
    }

    @Test
    public void testThrowsJsonProcessingException() throws ClientProtocolException, IOException {
        when(objectMapper.writeValueAsString(discrepancy))
                        .thenThrow(new JsonProcessingException("") {
                            private static final long serialVersionUID = 1L;});

        assertFalse(pscDiscrepancyFoundListenerImpl.created(discrepancy));
    }

    @Test
    public void testThrowsIOException() throws ClientProtocolException, IOException {
        when(objectMapper.writeValueAsString(discrepancy)).thenReturn("");
        when(client.execute(any(HttpPost.class))).thenThrow(new IOException());

        assertFalse(pscDiscrepancyFoundListenerImpl.created(discrepancy));
    }
}
