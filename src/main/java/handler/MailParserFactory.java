package handler;

import java.io.InputStream;
import javax.mail.MessagingException;
import parser.MailParser;

public class MailParserFactory {
    public MailParser createMailParser(InputStream in) throws MessagingException {
        return new MailParser(in);
    }
}
