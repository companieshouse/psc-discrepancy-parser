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
import org.junit.jupiter.api.Test;
import parser.MailParser;

class MailParserTest {
    private static final String EXPECTED =
                    "\"one\",\"two\",\"three\"\r\n\"four\",\"five\",\"six\"\r\n";

    @Test
    void mailContainingCsvIsDecodable() throws MessagingException, IOException {
        InputStream msgIs = getFileInputStream("src/test/resources/smallCsv.eml");
        MailParser mailParser = new MailParser(msgIs);
        byte[] extractedCsvAttachment = mailParser.extractCsvAttachment();
        String extractedCsvAsString = new String(extractedCsvAttachment);
        assertTrue(extractedCsvAsString.contains(EXPECTED));
    }

    @Test
    void mailContainingMultipleCsvIsDecodableAndFirstCsvFileIsUsedOthersIgnored() throws MessagingException, IOException {
        InputStream msgIs = getFileInputStream("src/test/resources/multiCsv.eml");
        MailParser mailParser = new MailParser(msgIs);
        byte[] extractedCsvAttachment = mailParser.extractCsvAttachment();
        String extractedCsvAsString = new String(extractedCsvAttachment);
        assertTrue(extractedCsvAsString.contains(EXPECTED));
    }


    @Test
    void mailContainingCsvWithUpperCaseFilenameCsvExtensionIsDecodable()
                    throws MessagingException, IOException {
        // The filename in this email ends in ".CSV", not ".csv"
        InputStream msgIs = getFileInputStream("src/test/resources/smallCsvWithUpperCSV.eml");
        MailParser mailParser = new MailParser(msgIs);
        byte[] extractedCsvAttachment = mailParser.extractCsvAttachment();
        String extractedCsvAsString = new String(extractedCsvAttachment);
        assertTrue(extractedCsvAsString.contains(EXPECTED));
    }


    @Test
    void mailContainingNoCsvThrowsIllegalArgEx() throws MessagingException, IOException {
        InputStream msgIs = getFileInputStream("src/test/resources/noCsvAttached.eml");
        MailParser mailParser = new MailParser(msgIs);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            mailParser.extractCsvAttachment();
        });
    }

    @Test
    void mailThatIsNotMultipartThrowsIllegalArgEx() throws MessagingException, IOException {
        InputStream msgIs = getFileInputStream("src/test/resources/notMultipart.eml");
        MailParser mailParser = new MailParser(msgIs);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            mailParser.extractCsvAttachment();
        });
    }

    // There is no 'corrupt BASE64' test, as illegal chars in BASE64 MIME are ignored.

    private static InputStream getFileInputStream(String filename) throws FileNotFoundException {
        File fl = new File(filename);
        FileInputStream fin = new FileInputStream(fl);
        return new BufferedInputStream(fin);
    }

}
