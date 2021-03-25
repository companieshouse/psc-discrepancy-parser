package util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.mail.MessagingException;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.fasterxml.jackson.databind.ObjectMapper;

import handler.PscDiscrepancySurveySender;
import model.PscDiscrepancySurvey;
import parser.CsvExtractor;
import parser.CsvProcessor;
import parser.CsvProcessor.CsvProcessorListener;

/**
 * Utility to be used when examining bugs in the main code. Given a file that is an email, or a CSV,
 * the methods in this class can be used to run the parser parts of this project to see what blows
 * up.
 * <h2>An example</h2>
 * 
 * <pre>
 * <code>
* {@code
* // Extract CSV attachment from email, print out the CSV to stdout, foreach CSV record,
* // turn it into JSON, printing out that JSON
* // and each JSON record
* extractCsvProcessAndDump(pathToEmail);
* 
* // Extract CSV attachment from email, print out the CSV to stdout
* extractCsvFromEmailAndDump(pathToEmail);
* 
* // Read a CSV file, parse it, turning each CSV record into
* // JSON, printing out that JSON
* byte[] csvBytes = getBytesFromFile(pathToCsv);
* processCsvAndDump(csvBytes);
* }
* // Read a CSV file, parse it, turning each CSV record into
* // JSON, posting that JSON to postUri
* String postUri = "http://some.host/some/path";
* byte[] csvBytes = getBytesFromFile(pathToCsv);
* processCsvAndPost(csvBytes, postUri);
* </code>
 * </pre>
 */
public class SupportUtils {
    /**
     * Listener for PscDiscrepancySurvey created events, which prints each discrepancy survey to
     * stdout.
     */
    private static class PscDiscrepancyDumpingListener implements CsvProcessorListener {
        @Override
        public boolean created(PscDiscrepancySurvey discrepancy) {
            System.out.println(discrepancy);
            System.out.println("------------------");
            return true;
        }
    }

    public static void main(String[] args) {
        try {
            // An example. See Javadocs for what to replace this with.
            String csvPath = "src/test/resources/oneGoodRecord.csv";
            byte[] cvsvBytesFromFile = getBytesFromFile(csvPath);
            processCsvAndDump(cvsvBytesFromFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Given a path to a local file, attempts to parse that file as an email, extract a CSV
     * attachment, print that CSV attachment to sdout, parse the CSV, transforming each record into
     * JSON, printing each JSON object to stdout.
     * 
     * @param emailPath
     * @throws MessagingException
     * @throws IOException
     */
    public static void extractCsvProcessAndDump(String emailPath)
                    throws MessagingException, IOException {
        byte[] csvBytes = extractCsvFromEmail(emailPath);
        processCsvAndDump(csvBytes);
    }

    /**
     * Given a path to a local file, attempts to parse that file as an email, extract a CSV
     * attachment, print that CSV attachment to sdout, parse the CSV, transforming each record into
     * JSON, printing each JSON object to stdout.
     * 
     * @param emailPath
     * @throws MessagingException
     * @throws IOException
     */
    public static byte[] extractCsvFromEmailAndDump(String filename)
                    throws MessagingException, IOException {
        byte[] parsedCsvBytes = extractCsvFromEmail(filename);
        String csv = new String(parsedCsvBytes, StandardCharsets.UTF_8);
        System.out.println("Extracted CSV: " + csv);
        return parsedCsvBytes;
    }

    /**
     * Given a path to a local file, attempts to parse that file as an email, extract a CSV
     * attachment, print that CSV attachment to sdout, finally returning the attachment as a byte
     * array, which can be fed to {@link #processCsvAndDump(byte[])}.
     * 
     * @param emailPath
     * @throws MessagingException
     * @throws IOException
     */
    public static byte[] extractCsvFromEmail(String filename)
                    throws MessagingException, IOException {
        InputStream is = getFileInputStream(filename);
        CsvExtractor mp = new CsvExtractor(is);
        return mp.extractCsvAttachment();
    }

    /**
     * Given a byte[] bytes, attempt to parse those bytes as CSV, transforming each record into
     * JSON, printing each JSON object to stdout.
     * 
     * @param bytes
     * @return
     * @throws IOException
     */
    public static boolean processCsvAndDump(byte[] bytes) throws IOException {
        CsvProcessor processor = new CsvProcessor(bytes,
                        new PscDiscrepancyDumpingListener());
        return processor.parseRecords();
    }

    /**
     * Given a byte[] bytes, attempt to parse those bytes as CSV, transforming each record into
     * JSON, separately posting that JSON to postUri.
     * @param bytes
     * @param postUri
     * @return
     * @throws IOException
     */
    public static boolean processCsvAndPost(byte[] bytes, String postUri) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String fakeRequestId = "" + System.currentTimeMillis();
            System.out.println("About to process CSV and assign requestId: " + fakeRequestId);
            PscDiscrepancySurveySender listener =
                    new PscDiscrepancySurveySender(httpClient, postUri,
                            new ObjectMapper(), fakeRequestId);
            CsvProcessor processor = new CsvProcessor(bytes, listener);
            return processor.parseRecords();
        }
    }

    /**
     * Given the path to a file, return an InputStream for that file.
     * 
     * @param filename
     * @return
     * @throws FileNotFoundException
     */
    private static InputStream getFileInputStream(String filename) throws FileNotFoundException {
        File fl = new File(filename);
        FileInputStream fin = new FileInputStream(fl);
        return new BufferedInputStream(fin);
    }

    /**
     * Given a path to a file, fully read that file into a byte array, which is returned.
     * 
     * @param filename
     * @return
     * @throws IOException
     */
    private static byte[] getBytesFromFile(String filename) throws IOException {
        Path fileLocation = Paths.get(filename);
        return Files.readAllBytes(fileLocation);
    }
}
