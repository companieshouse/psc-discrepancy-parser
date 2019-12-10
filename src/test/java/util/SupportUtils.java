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
import model.PscDiscrepancySurvey;
import parser.MailParser;
import parser.PscDiscrepancySurveyCsvProcessor;
import parser.PscDiscrepancySurveyCsvProcessor.PscDiscrepancyCreatedListener;

public class SupportUtils {
    private static class PscDiscrepancyDumpingListener implements PscDiscrepancyCreatedListener {
        @Override
        public boolean created(PscDiscrepancySurvey discrepancy) {
            System.out.println(discrepancy);
            System.out.println("------------------");
            return true;
        }
    }

    public static void main(String[] args) {
        try {
            // String emailPath = "";
            String csvPath = "src/test/resources/oneGoodRecord.csv";
            // byte[] extractedCsv = extractCsvFromEmailAndDump(emailPath);
            // processCsvAndDump(extractedCsv);
            // extractCsvProcessAndDump(emailPath);
            byte[] cvsvBytesFromFile = getBytesFromFile(csvPath);
            processCsvAndDump(cvsvBytesFromFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void extractCsvProcessAndDump(String emailPath)
                    throws MessagingException, IOException {
        byte[] csvBytes = extractCsvFromEmail(emailPath);
        processCsvAndDump(csvBytes);
    }

    public static byte[] extractCsvFromEmailAndDump(String filename)
                    throws MessagingException, IOException {
        byte[] parsedCsvBytes = extractCsvFromEmail(filename);
        String csv = new String(parsedCsvBytes, StandardCharsets.UTF_8);
        System.out.println("Extracted CSV: " + csv);
        return parsedCsvBytes;
    }

    public static byte[] extractCsvFromEmail(String filename)
                    throws MessagingException, IOException {
        InputStream is = getFileInputStream(filename);
        MailParser mp = new MailParser(is);
        return mp.extractCsvAttachment();
    }

    public static boolean processCsvAndDump(byte[] bytes) throws IOException {
        PscDiscrepancySurveyCsvProcessor processor = new PscDiscrepancySurveyCsvProcessor(bytes,
                        new PscDiscrepancyDumpingListener());
        return processor.parseRecords();
    }

    private static InputStream getFileInputStream(String filename) throws FileNotFoundException {
        File fl = new File(filename);
        FileInputStream fin = new FileInputStream(fl);
        return new BufferedInputStream(fin);
    }

    private static byte[] getBytesFromFile(String filename) throws IOException {
        Path fileLocation = Paths.get(filename);
        return Files.readAllBytes(fileLocation);
    }
}
