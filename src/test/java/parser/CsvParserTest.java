package parser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.PscDiscrepancySurvey;
import parser.PscDiscrepancySurveyCsvProcessor.PscDiscrepancyCreatedListener;

@ExtendWith(MockitoExtension.class)
class CsvParserTest {
    @Mock
    private PscDiscrepancyCreatedListener listener;

    @Captor
    ArgumentCaptor<PscDiscrepancySurvey> PscDiscrepancySurveyArg;

    @Test
    void emptyFileMustFailToParse() throws IOException {
        byte[] bytes = getFile("src/test/resources/empty.csv");
        PscDiscrepancySurveyCsvProcessor parser =
                        new PscDiscrepancySurveyCsvProcessor(bytes, listener);
        assertFalse(parser.parseRecords());
    }

    @Test
    void csvFileWithTooFewHeadersMustFailToParse() throws IOException {
        byte[] bytes = getFile("src/test/resources/tooFewHeaders.csv");
        PscDiscrepancySurveyCsvProcessor parser =
                        new PscDiscrepancySurveyCsvProcessor(bytes, listener);
        assertFalse(parser.parseRecords());
    }

    @Test
    void csvFileWithOnlyHeadersMustFailToParse() throws IOException {
        byte[] bytes = getFile("src/test/resources/onlyHeaders.csv");
        PscDiscrepancySurveyCsvProcessor parser =
                        new PscDiscrepancySurveyCsvProcessor(bytes, listener);
        assertFalse(parser.parseRecords());
    }

    @Test
    void csvRecordWithTooFewColumnsMustFailToParse() throws IOException {
        byte[] bytes = getFile("src/test/resources/tooFewColumns.csv");
        PscDiscrepancySurveyCsvProcessor parser =
                        new PscDiscrepancySurveyCsvProcessor(bytes, listener);
        assertFalse(parser.parseRecords());
    }

    @Test
    void csvRecordWithTooManyColumnsMustFailToParse() throws IOException {
        byte[] bytes = getFile("src/test/resources/tooManyColumns.csv");
        PscDiscrepancySurveyCsvProcessor parser =
                        new PscDiscrepancySurveyCsvProcessor(bytes, listener);
        assertFalse(parser.parseRecords());
    }

    @Test
    void badDiscrepancyIdentifiedOnMustFailToParse() throws IOException {
        byte[] bytes = getFile("src/test/resources/badDate.csv");
        PscDiscrepancySurveyCsvProcessor parser =
                        new PscDiscrepancySurveyCsvProcessor(bytes, listener);
        assertFalse(parser.parseRecords());
    }

    @Test
    void oneGoodRecordMustParse() throws IOException {
        byte[] bytes = getFile("src/test/resources/oneGoodRecord.csv");
        PscDiscrepancySurvey expected = readSurvey("src/test/resources/oneGoodRecord.json");
        when(listener.created(expected)).thenReturn(true);
        PscDiscrepancySurveyCsvProcessor parser =
                        new PscDiscrepancySurveyCsvProcessor(bytes, listener);
        assertTrue(parser.parseRecords());
    }

    @Test
    void quotedCommasMustNotBlowUpParser() throws IOException {
        byte[] bytes = getFile("src/test/resources/escapedCommas.csv");
        PscDiscrepancySurvey expected = readSurvey("src/test/resources/escapedCommas.json");
        when(listener.created(expected)).thenReturn(true);
        PscDiscrepancySurveyCsvProcessor parser =
                        new PscDiscrepancySurveyCsvProcessor(bytes, listener);
        assertTrue(parser.parseRecords());
    }

    @Test
    void quotedEscapedQuotesMustNotBlowUpParserAndMustBeUnescapedByParsing() throws IOException {
        byte[] bytes = getFile("src/test/resources/escapedQuotes.csv");
        PscDiscrepancySurvey expected = readSurvey("src/test/resources/escapedQuotes.json");
        when(listener.created(expected)).thenReturn(true);
        PscDiscrepancySurveyCsvProcessor parser =
                        new PscDiscrepancySurveyCsvProcessor(bytes, listener);
        assertTrue(parser.parseRecords());
    }

    @Test
    void quotedNewlinesMustNotBlowUpParser() throws IOException {
        byte[] bytes = getFile("src/test/resources/quotedNewlines.csv");
        PscDiscrepancySurvey expected = readSurvey("src/test/resources/quotedNewlines.json");
        when(listener.created(expected)).thenReturn(true);
        PscDiscrepancySurveyCsvProcessor parser =
                        new PscDiscrepancySurveyCsvProcessor(bytes, listener);
        assertTrue(parser.parseRecords());
    }

    @Test
    void badCsvMustFailToParse() throws IOException {
        byte[] bytes = getFile("src/test/resources/badCsv.csv");
        PscDiscrepancySurveyCsvProcessor parser =
                        new PscDiscrepancySurveyCsvProcessor(bytes, listener);
        assertFalse(parser.parseRecords());
    }

    @Test
    void failingCallbackShowsAsFailureToParse() throws IOException {
        byte[] bytes = getFile("src/test/resources/quotedNewlines.csv");
        PscDiscrepancySurvey expected = readSurvey("src/test/resources/quotedNewlines.json");
        when(listener.created(expected)).thenReturn(false);
        PscDiscrepancySurveyCsvProcessor parser =
                        new PscDiscrepancySurveyCsvProcessor(bytes, listener);
        assertFalse(parser.parseRecords());
    }

    private static PscDiscrepancySurvey readSurvey(String filename)
                    throws JsonParseException, JsonMappingException, IOException {
        return new ObjectMapper().readValue(new File(filename), PscDiscrepancySurvey.class);
    }

    private static byte[] getFile(String filename) throws IOException {
        Path fileLocation = Paths.get(filename);
        return Files.readAllBytes(fileLocation);
    }
}
