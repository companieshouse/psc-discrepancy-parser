package parser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Decoder;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility to extract a BASE64-encoded CSV attachment from a multipart/mixed email.
 */
public class MailParser {
    private static final Session NO_SESSION = null;
    private static final String CASE_INSENSITIVE_CSV_REGEX = ".+\\.[cC][sS][vV]$";
    private static final Logger LOG = LogManager.getLogger(MailParser.class);
    private final MimeMessage msg;

    /**
     * Constructor that takes an InputStream from which the mail will be read and parses the email
     * as a MIME message.
     * 
     * @param is The input stream.
     * @throws MessagingException if the email is corrupt in some way.
     */
    public MailParser(InputStream is) throws MessagingException {
        msg = new MimeMessage(NO_SESSION, is);
    }

    /**
     * If the email loaded in the constructor contains an attachment with a filename that ends in
     * ".csv"
     * 
     * @return the extracted CSV attachment
     * @throws MessagingException If there is a problem parsing the email.
     * @throws IOException If there is a problem reading from the InputStream.
     * @throws IllegalArgumentException If the attachment cannot be found, or if the email is not
     *         multipart/mixed, or if the email has no Content-Type.
     */
    public byte[] extractCsvAttachment() throws MessagingException, IOException {
        try {
            boolean found = false;
            byte[] result = new byte[] {};
            String contentType = msg.getContentType();
            if (contentType == null) {
                throw new MessagingException("Could not find Content-Type");
            }
            if (!contentType.startsWith("multipart/mixed")) {
                throw new MessagingException("Not multipart/mixed:\n" + getMsgAsString());
            }
            String messageID = msg.getMessageID();
            Address[] from = msg.getFrom();
            String[] date = msg.getHeader("Date");
            String[] subject = msg.getHeader("Subject");
            // TODO: log above.
            Multipart multiPart = (Multipart) msg.getContent();
            int numberOfParts = multiPart.getCount();
            for (int partCount = 0; partCount < numberOfParts; partCount++) {
                MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                    String fileName = part.getFileName();
                    String encoding = part.getEncoding();
                    if (fileName != null && fileName.matches(CASE_INSENSITIVE_CSV_REGEX)
                                    && encoding != null && "base64".equals(encoding)) {
                        InputStream partRawIs = part.getRawInputStream();
                        byte[] base64Encoded = IOUtils.toByteArray(partRawIs);
                        String base64 = new String(base64Encoded);
                        Decoder mimeDecoder = Base64.getMimeDecoder();
                        result = mimeDecoder.decode(base64);
                        found = true;
                        break;
                    }
                } // else skip this body part, not an attachment
            }
            if (!found) {
                LOG.error("Could not find attachment of type CSV");
                throw new MessagingException("Could not find attachment of type CSV");
            }
            return result;
        } finally {
            msg.getInputStream().close();
        }
    }

    private String getMsgAsString() throws IOException, MessagingException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        msg.writeTo(out);
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }
}
