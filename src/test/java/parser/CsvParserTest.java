package parser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
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
import parser.PscDiscrepancySurveyCsvProcessor.PscDiscrepancyFoundListener;

@ExtendWith(MockitoExtension.class)
class CsvParserTest {
    @Mock
    private PscDiscrepancyFoundListener listener;

    @Captor
    ArgumentCaptor<PscDiscrepancySurvey> PscDiscrepancySurveyArg;

    @Test
    void emptyFileMustFailToParse() throws IOException {
        Reader fileReader = getFileReader("src/test/resources/empty.csv");
        PscDiscrepancySurveyCsvProcessor parser = new PscDiscrepancySurveyCsvProcessor(fileReader, listener);
        assertFalse(parser.parseRecords());
    }

    @Test
    void csvFileWithTooFewHeadersMustFailToParse() throws IOException {
        Reader fileReader = getFileReader("src/test/resources/tooFewHeaders.csv");
        PscDiscrepancySurveyCsvProcessor parser = new PscDiscrepancySurveyCsvProcessor(fileReader, listener);
        assertFalse(parser.parseRecords());
    }

    @Test
    void csvFileWithOnlyHeadersMustFailToParse() throws IOException {
        Reader fileReader = getFileReader("src/test/resources/onlyHeaders.csv");
        PscDiscrepancySurveyCsvProcessor parser = new PscDiscrepancySurveyCsvProcessor(fileReader, listener);
        assertFalse(parser.parseRecords());
    }

    @Test
    void csvRecordWithTooFewColumnsMustFailToParse() throws IOException {
        Reader fileReader = getFileReader("src/test/resources/tooFewColumns.csv");
        PscDiscrepancySurveyCsvProcessor parser = new PscDiscrepancySurveyCsvProcessor(fileReader, listener);
        assertFalse(parser.parseRecords());
    }

    @Test
    void csvRecordWithTooManyColumnsMustFailToParse() throws IOException {
        Reader fileReader = getFileReader("src/test/resources/tooManyColumns.csv");
        PscDiscrepancySurveyCsvProcessor parser = new PscDiscrepancySurveyCsvProcessor(fileReader, listener);
        assertFalse(parser.parseRecords());
    }

    @Test
    void badDiscrepancyIdentifiedOnMustFailToParse() throws IOException {
        Reader fileReader = getFileReader("src/test/resources/badDate.csv");
        PscDiscrepancySurveyCsvProcessor parser = new PscDiscrepancySurveyCsvProcessor(fileReader, listener);
        assertFalse(parser.parseRecords());
    }

    @Test
    void oneGoodRecordMustParse() throws IOException {
        Reader fileReader = getFileReader("src/test/resources/oneGoodRecord.csv");
        PscDiscrepancySurvey expected = readSurvey("src/test/resources/oneGoodRecord.json");
        when(listener.parsed(expected)).thenReturn(true);
        PscDiscrepancySurveyCsvProcessor parser = new PscDiscrepancySurveyCsvProcessor(fileReader, listener);
        assertTrue(parser.parseRecords());
    }

    @Test
    void quotedCommasMustNotBlowUpParser() throws IOException {
        Reader fileReader = getFileReader("src/test/resources/escapedCommas.csv");
        PscDiscrepancySurvey expected = readSurvey("src/test/resources/escapedCommas.json");
        when(listener.parsed(expected)).thenReturn(true);
        PscDiscrepancySurveyCsvProcessor parser = new PscDiscrepancySurveyCsvProcessor(fileReader, listener);
        assertTrue(parser.parseRecords());
    }

    @Test
    void quotedEscapedQuotesMustNotBlowUpParserAndMustBeUnescapedByParsing() throws IOException {
        Reader fileReader = getFileReader("src/test/resources/escapedQuotes.csv");
        PscDiscrepancySurvey expected = readSurvey("src/test/resources/escapedQuotes.json");
        when(listener.parsed(expected)).thenReturn(true);
        PscDiscrepancySurveyCsvProcessor parser = new PscDiscrepancySurveyCsvProcessor(fileReader, listener);
        assertTrue(parser.parseRecords());
    }

    @Test
    void quotedNewlinesMustNotBlowUpParser() throws IOException {
        Reader fileReader = getFileReader("src/test/resources/quotedNewlines.csv");
        PscDiscrepancySurvey expected = readSurvey("src/test/resources/quotedNewlines.json");
        when(listener.parsed(expected)).thenReturn(true);
        PscDiscrepancySurveyCsvProcessor parser = new PscDiscrepancySurveyCsvProcessor(fileReader, listener);
        assertTrue(parser.parseRecords());
    }

    @Test
    void badCsvMustFailToParse() throws IOException {
        Reader fileReader = getFileReader("src/test/resources/badCsv.csv");
        PscDiscrepancySurveyCsvProcessor parser = new PscDiscrepancySurveyCsvProcessor(fileReader, listener);
        assertFalse(parser.parseRecords());
    }

    @Test
    void failingCallbackShowsAsFailureToParse() throws IOException {
        Reader fileReader = getFileReader("src/test/resources/quotedNewlines.csv");
        PscDiscrepancySurvey expected = readSurvey("src/test/resources/quotedNewlines.json");
        when(listener.parsed(expected)).thenReturn(false);
        PscDiscrepancySurveyCsvProcessor parser = new PscDiscrepancySurveyCsvProcessor(fileReader, listener);
        assertFalse(parser.parseRecords());
    }

    private static PscDiscrepancySurvey readSurvey(String filename) throws JsonParseException, JsonMappingException, IOException {
        return new ObjectMapper().readValue(new File(filename), PscDiscrepancySurvey.class);
    }
    private static InputStream getFileInputStream(String filename) throws FileNotFoundException {
        File fl = new File(filename);
        FileInputStream fin = new FileInputStream(fl);
        return new BufferedInputStream(fin);
    }

    private static Reader getFileReader(String filename) throws FileNotFoundException {
        InputStream fin = getFileInputStream(filename);
        return new InputStreamReader(fin, StandardCharsets.UTF_8);
    }
}
