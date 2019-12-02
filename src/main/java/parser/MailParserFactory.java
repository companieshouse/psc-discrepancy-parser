package parser;

import java.io.InputStream;
import javax.mail.MessagingException;

public class MailParserFactory {
    public MailParser createMailParser(InputStream in) throws MessagingException {
        return new MailParser(in);
    }
}
