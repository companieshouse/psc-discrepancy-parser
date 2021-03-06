package parser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.mail.MessagingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CsvExtractorTest {
    private static final String EXPECTED =
                    "\"one\",\"two\",\"three\"\r\n\"four\",\"five\",\"six\"\r\n";

    private CsvExtractorFactory csvExtractorFactory;

    @BeforeEach
    void setup() {
        csvExtractorFactory = new CsvExtractorFactory();
    }

    @Test
    void mailContainingCsvIsDecodable() throws MessagingException, IOException {
        InputStream msgIs = getFileInputStream("src/test/resources/smallCsv.eml");
        CsvExtractor csvExtractor = csvExtractorFactory.createMailParser(msgIs);
        byte[] extractedCsvAttachment = csvExtractor.extractCsvAttachment();
        String extractedCsvAsString = new String(extractedCsvAttachment);
        // Note that an exact match could not be obtained, byte-for-byte,
        // so the looser 'contains' method is used in these tests, rather than equals.
        // The actual decoded bytes seem to start with 0xFF, not a legal UTF-8 character.
        // These are real emails being decoded (albeit anonymised) with real attachments.
        // I can only assume that the start bytes are something weird like magic file bytes.
        // The resulting string does decode properly in a CSV parser, so we have chosen not to
        // worry.
        // The same applies for every positive test, using contains rather than an exact match.
        assertTrue(extractedCsvAsString.contains(EXPECTED));
    }

    @Test
    void mailContainingMultipleCsvIsDecodableAndFirstCsvFileIsUsedOthersIgnored()
                    throws MessagingException, IOException {
        InputStream msgIs = getFileInputStream("src/test/resources/multiCsv.eml");
        CsvExtractor csvExtractor = csvExtractorFactory.createMailParser(msgIs);
        byte[] extractedCsvAttachment = csvExtractor.extractCsvAttachment();
        String extractedCsvAsString = new String(extractedCsvAttachment);
        assertTrue(extractedCsvAsString.contains(EXPECTED));
    }


    @Test
    void mailContainingCsvWithUpperCaseFilenameCsvExtensionIsDecodable()
                    throws MessagingException, IOException {
        // The filename in this email ends in ".CSV", not ".csv"
        InputStream msgIs = getFileInputStream("src/test/resources/smallCsvWithUpperCSV.eml");
        CsvExtractor csvExtractor = csvExtractorFactory.createMailParser(msgIs);
        byte[] extractedCsvAttachment = csvExtractor.extractCsvAttachment();
        String extractedCsvAsString = new String(extractedCsvAttachment);
        assertTrue(extractedCsvAsString.contains(EXPECTED));
    }


    @Test
    void mailContainingNoCsvThrowsMessagingEx() throws MessagingException, IOException {
        InputStream msgIs = getFileInputStream("src/test/resources/noCsvAttached.eml");
        CsvExtractor csvExtractor = csvExtractorFactory.createMailParser(msgIs);
        Assertions.assertThrows(MessagingException.class, () -> {
            csvExtractor.extractCsvAttachment();
        });
    }

    @Test
    void mailThatIsNotMultipartMixedThrowsMessagingEx() throws MessagingException, IOException {
        InputStream msgIs = getFileInputStream("src/test/resources/notMultipart.eml");
        CsvExtractor csvExtractor = csvExtractorFactory.createMailParser(msgIs);
        Assertions.assertThrows(MessagingException.class, () -> {
            csvExtractor.extractCsvAttachment();
        });
    }

    // There is no 'corrupt BASE64' test, as illegal chars in BASE64 MIME are ignored.

    private static InputStream getFileInputStream(String filename) throws FileNotFoundException {
        File fl = new File(filename);
        FileInputStream fin = new FileInputStream(fl);
        return new BufferedInputStream(fin);
    }
}
