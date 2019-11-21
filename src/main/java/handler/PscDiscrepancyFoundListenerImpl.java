package handler;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
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
        try {
            String discrepancyJson = objectMapper.writeValueAsString(discrepancy);

            lambdaLogger.log("Receive discrepancy: " + discrepancyJson);
            
//            DefaultHttpClient httpClient = new DefaultHttpClient();
//
//            HttpPost postRequest = new HttpPost(REST_URI);
//
//            StringEntity input = new StringEntity(discrepancyJson);
//            input.setContentType("text/plain");
//            postRequest.setEntity(input);
//
//            HttpResponse response = httpClient.execute(postRequest);
//
//            if (response.getStatusLine().getStatusCode() != 201) {
//                return false;
//            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

}
