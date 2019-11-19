package parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Base64.Decoder;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import org.apache.commons.io.IOUtils;

/**
 * Utility to extract a BASE64-encoded CSV attachment from a multipart/mixed email.
 */
public class MailParser {
    private static final Session NO_SESSION = null;
    private final MimeMessage msg;

    /**
     * Constructor that takes an InputStream from which the mail will be read and
     * parses the email as a MIME message.
     * 
     * @param is The input stream.
     * @throws MessagingException if the email is corrupt in some way.
     */
    public MailParser(InputStream is) throws MessagingException {
        msg = new MimeMessage(NO_SESSION, is);
    }


    /**
     * If the email loaded in the constructor contains an attachment with a filename that ends in ".csv"
     * 
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public byte[] extractCsvAttachment() throws MessagingException, IOException {
        boolean found = false;
        byte[] result = new byte[] {};
        String contentType = msg.getContentType();
        if (contentType == null) {
            throw new IllegalArgumentException("Could not find Content-Type");
        }
        if (!contentType.startsWith("multipart/mixed")) {
            throw new IllegalArgumentException("Not multipart/mixed: " + contentType);
        }
        Multipart multiPart = (Multipart) msg.getContent();
        int numberOfParts = multiPart.getCount();
        for (int partCount = 0; partCount < numberOfParts; partCount++) {
            MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
            if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                String fileName = part.getFileName();
                String encoding = part.getEncoding();
                if (fileName != null && fileName.matches(".+\\.[cC][sS][vV]$") && encoding != null
                                && "base64".equals(encoding)) {
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
            throw new IllegalArgumentException("Could not find attachment of type CSV");
        }
        return result;
    }
}
