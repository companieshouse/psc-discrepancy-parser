package handler;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.PscDiscrepancySurvey;
import parser.CsvParser.PscDiscrepancyFoundListener;

public class PscDiscrepancyFoundListenerImpl implements PscDiscrepancyFoundListener {

    private static String REST_URI = "http://localhost:8080/RESTfulExample/json/product/post";
    private final LambdaLogger lambdaLogger;

    public PscDiscrepancyFoundListenerImpl(LambdaLogger log) {
        this.lambdaLogger = log;
    }

    @Override
    public boolean parsed(PscDiscrepancySurvey discrepancy) {
        ObjectMapper objectMapper = new ObjectMapper();
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String discrepancyJson = objectMapper.writeValueAsString(discrepancy);

            lambdaLogger.log("Received discrepancy: " + discrepancyJson);

            HttpPost httpPost = new HttpPost("http://chpdev-pl6:21011/chips-restService/rest/chipsgeneric/pscDiscrepancies");
            StringEntity entity = new StringEntity(discrepancyJson);
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-type", "text/plain");
            CloseableHttpResponse response = client.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_ACCEPTED) {
                lambdaLogger.log("Successfully sent JSON");
                return true;
            } else {
                lambdaLogger.log("Failed to send JSON: " + response);
                return false;
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

}
