package handler;

import java.io.IOException;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.json.JsonSanitizer;
import model.PscDiscrepancySurvey;
import parser.CsvProcessor.CsvProcessorListener;

/**
 * Listens for the creation of a PscDiscrepancySurvey, converts it to JSON, and sends by HTTP POST
 * it to the supplied URL.
 *
 */
public class PscDiscrepancySurveySender implements CsvProcessorListener {

    private static final Logger LOG = LogManager.getLogger(PscDiscrepancySurveySender.class);
    private final CloseableHttpClient client;
    private final String postUrl;
    private final ObjectMapper objectMapper;
    private final String requestId;

    public PscDiscrepancySurveySender(CloseableHttpClient client, String postUrl,
                    ObjectMapper objectMapper, String requestId) {
        this.client = client;
        this.postUrl = postUrl;
        this.objectMapper = objectMapper;
        this.requestId = requestId;
    }

    @Override
    public boolean created(PscDiscrepancySurvey discrepancy) {
        try {
            discrepancy.setRequestId(requestId);
            String discrepancyJson = objectMapper.writeValueAsString(discrepancy);
            String sanitisedJson = JsonSanitizer.sanitize(discrepancyJson);
            StringEntity entity = new StringEntity(sanitisedJson);
            LOG.info("Callback for discrepancy: {}", sanitisedJson);
            HttpPost httpPost = new HttpPost(postUrl);
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-type", "text/plain");
            try (CloseableHttpResponse response = client.execute(httpPost)) {
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_ACCEPTED) {
                    LOG.info("Successfully sent JSON");
                    return true;
                } else {
                    LOG.error("Failed to send JSON: {}", response);
                    return false;
                }
            }
        } catch (JsonProcessingException e) {
            LOG.error("Error serialising to JSON: ", e);
            return false;
        } catch (IOException e) {
            LOG.error("Error serialising to JSON or sending payload: ", e);
            return false;
        }
    }
}
