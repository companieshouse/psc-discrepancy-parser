package parser;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.PscDiscrepancySurvey;
import parser.CsvParser.PscDiscrepancyFoundListener;

class CsvParserTest {
    //todo: rename PscXXX To PscSurveyXXX
    PscDiscrepancyFoundListener NO_OP_LISTENER = new PscDiscrepancyFoundListener() {
        @Override
        public boolean parsed(PscDiscrepancySurvey discrepancy) {
            ObjectMapper objectMapper = new ObjectMapper();
            String discrepancyJson;
            try {
                discrepancyJson = objectMapper.writeValueAsString(discrepancy);
                System.out.println(discrepancyJson);
                System.out.println("---------------------------------");
                System.out.println("---------------------------------");
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }
            return true;
        }
    };

    @Test
    void test() throws IOException, ParseException {
        Reader fileReader = getFileReader("src/test/resources/1035820.csv");
        
        CsvParser parser = new CsvParser(fileReader, NO_OP_LISTENER);
        boolean successFullyProcessedAll = parser.parseRecords();
        
    }

    private static InputStream getFileInputStream(String filename) throws FileNotFoundException {
        File fl = new File(filename);
        FileInputStream fin = new FileInputStream(fl);
        return new BufferedInputStream(fin);
    }

    private static Reader getFileReader(String filename) throws FileNotFoundException, UnsupportedEncodingException {
        InputStream fin = getFileInputStream(filename);
        return new InputStreamReader(fin, "UTF-8");
    }

    private static Reader wrapByteArrayInReader(byte[] bytes) {
        ByteArrayInputStream decodedBase64AsStream = new ByteArrayInputStream(bytes);
        return new InputStreamReader(decodedBase64AsStream);
    }

}
