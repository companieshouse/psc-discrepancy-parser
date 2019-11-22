package handler;

import java.io.IOException;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.PscDiscrepancySurvey;
import parser.CsvParser.PscDiscrepancyFoundListener;

public class PscDiscrepancyFoundListenerImpl implements PscDiscrepancyFoundListener {

    //private static String REST_URI = "http://localhost:8080/RESTfulExample/json/product/post";
    private static final Logger LOG = LogManager.getLogger(PscDiscrepancyFoundListenerImpl.class);

    @Override
    public boolean parsed(PscDiscrepancySurvey discrepancy) {
        ObjectMapper objectMapper = new ObjectMapper();
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String discrepancyJson = objectMapper.writeValueAsString(discrepancy);

            LOG.error("Callback for discrepancy: {}", discrepancyJson);

            HttpPost httpPost = new HttpPost("http://chpdev-pl6:21011/chips-restService/rest/chipsgeneric/pscDiscrepancies");
            StringEntity entity = new StringEntity(discrepancyJson);
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-type", "text/plain");
            CloseableHttpResponse response = client.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_ACCEPTED) {
                LOG.error("Successfully sent JSON");
                return true;
            } else {
                LOG.error("Failed to send JSON: {}", response);
                return false;
            }
        } catch (JsonProcessingException e) {
            LOG.error("Error serialising to JSON: ", e);
            return false;
        } catch (IOException e) {
            LOG.error("Error serialising to JSON or sending payload: ", e);
        }
        return true;
    }

}
