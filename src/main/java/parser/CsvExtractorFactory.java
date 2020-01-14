package parser;

import java.io.InputStream;
import javax.mail.MessagingException;

public class CsvExtractorFactory {
    public CsvExtractor createMailParser(InputStream in) throws MessagingException {
        return new CsvExtractor(in);
    }
}
