package parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import model.PscDiscrepancySurvey;
import model.PscDiscrepancySurveyObligedEntity;
import model.PscDiscrepancySurveyQandA;
import model.PscDiscrepancySurveyQuestion;

public class CsvParser {
    private static final int INITIAL_LINES_TO_IGNORE = 3;
    private static final String NULL_FIELD = "-";
    private static final int CORRECT_COLUMN_COUNT = 100;
    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private final List<CSVRecord> successfullyParsedLines = new ArrayList<>();
    private final List<CSVRecord> failedToBeParsedLines = new ArrayList<>();
    private final Reader reader;
    private static final Logger logger = LogManager.getLogger(CsvParser.class);
    private final PscDiscrepancyFoundListener listener;
    private boolean successfullyProcessedSoFar = true;
    private int currentRecordBeingParsed = -1;

    public interface PscDiscrepancyFoundListener {
        boolean parsed(PscDiscrepancySurvey discrepancy);
    }

    public CsvParser(Reader reader, PscDiscrepancyFoundListener listener) {
        this.reader = reader;
        this.listener = listener;
    }

    public CsvParser(byte[] bytesToParse, PscDiscrepancyFoundListener listener) {
        this.listener = listener;
        ByteArrayInputStream decodedBase64AsStream = new ByteArrayInputStream(bytesToParse);
        reader = new InputStreamReader(decodedBase64AsStream);
    }

    public boolean parseRecords() throws IOException {
        Iterator<CSVRecord> it = null;
        try {
            Iterable<CSVRecord> records =
                            CSVFormat.DEFAULT.withNullString(NULL_FIELD).parse(reader);
            it = records.iterator();
            if (moveToStartOfData(it, INITIAL_LINES_TO_IGNORE)) {
                while (it.hasNext()) {
                    currentRecordBeingParsed++;
                    parseRecord(it.next());
                }
            } else {
                successfullyProcessedSoFar = false;
            }
        } catch (RuntimeException ex) {
            logger.error("Unexpected runtime exception caught while parsing record[: {}" + ex, ex);
            if (it != null) {
                while (it.hasNext()) {
                    failedToBeParsedLines.add(it.next());
                    // TODO: record counter
                }
            }
            successfullyProcessedSoFar = false;
        }
        return successfullyProcessedSoFar;
    }

    void parseRecord(CSVRecord record) {
        PscDiscrepancySurvey discrepancy = new PscDiscrepancySurvey();
        boolean successfullyParsed = checkColumnCount(record)
                        && parseDiscrepancyBasicDetails(record, discrepancy)
                        && parseObligedEntity(record, discrepancy)
                        && parseQandAs(record, discrepancy);
        if (successfullyParsed) {
            boolean listenerCallbackSuccess = listener.parsed(discrepancy);
            if (listenerCallbackSuccess) {
                onRecordSuccessfullyProcessed(record);
            } else {
                onRecordFailedToBeProcessed(record);
            }
        } else {
            onRecordFailedToBeProcessed(record);
        }
    }

    boolean parseObligedEntity(CSVRecord record, PscDiscrepancySurvey discrepancy) {
        PscDiscrepancySurveyObligedEntity oe = new PscDiscrepancySurveyObligedEntity();
        oe.setCompanyName(record.get(0));
        oe.setObligedEntityType(record.get(1));
        oe.setContactName(record.get(3));
        oe.setContactEmail(record.get(4));
        oe.setContactPhone(record.get(5));
        oe.setContactAddressLine1(record.get(6));
        oe.setContactAddressLine2(record.get(7));
        oe.setContactAddressLine3(record.get(8));
        oe.setContactAddressLine4(record.get(9));
        oe.setContactAddressLine5(record.get(10));
        oe.setContactAddressLine6(record.get(11));
        oe.setContactAddressPostCode(record.get(12));
        discrepancy.setObligedEntity(oe);
        return true;
    }

    boolean parseDiscrepancyBasicDetails(CSVRecord record, PscDiscrepancySurvey discrepancy) {
        discrepancy.setCompanyName(record.get(13));
        discrepancy.setCompanyNumber(record.get(14));

        discrepancy.setDiscrepancyType(record.get(15));

        String discrepancyIdentifiedOnStr = record.get(2);
        if (discrepancyIdentifiedOnStr != null) {
            SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
            try {
                Date discrepancyIdentifiedOn = format.parse(discrepancyIdentifiedOnStr);
                discrepancy.setDiscrepancyIdentifiedOn(discrepancyIdentifiedOn);
            } catch (ParseException ex) {
                logger.error("Could not parse discrepancyIdentifiedOnStr: %s",
                                discrepancyIdentifiedOnStr, ex);
                return false;
            }
        }
        return true;
    }

    boolean parseQandAs(CSVRecord record, PscDiscrepancySurvey discrepancy) {
        int size = record.size();
        List<PscDiscrepancySurveyQandA> qas = new ArrayList<>();
        boolean foundUnknownQuestion = false;
        for (int i = 16; i < size; i++) {
            String a = record.get(i);
            if (a != null) {
                PscDiscrepancySurveyQuestion q = PscDiscrepancySurveyQuestion.getByZeroIndexId(i);
                if (q == PscDiscrepancySurveyQuestion.UNKNOWN) {
                    logger.error("Given column number, could not find question: {}", i);
                    foundUnknownQuestion = true;
                    break;
                } else {
                    PscDiscrepancySurveyQandA qa = new PscDiscrepancySurveyQandA();
                    qa.setQuestion(q);
                    qa.setAnswer(a);
                    qas.add(qa);
                }
            }
        }
        if (foundUnknownQuestion) {
            return false;
        } else {
            discrepancy.setQuestionsAndAnswers(qas);
            return true;
        }
    }

    void onGeneralFailure() {
        successfullyProcessedSoFar = false;
    }

    void onRecordSuccessfullyProcessed(CSVRecord record) {
        successfullyParsedLines.add(record);
    }

    void onRecordFailedToBeProcessed(CSVRecord record) {
        failedToBeParsedLines.add(record);
        successfullyProcessedSoFar = false;
    }

    boolean checkColumnCount(CSVRecord record) {
        if (CORRECT_COLUMN_COUNT != record.size()) {
            logger.error("Unexpected number of columns in CSV record: %s", record.size());
            return false;
        }
        return true;
    }

    boolean moveToStartOfData(Iterator<CSVRecord> it, int linesToIgnore) {
        boolean success = true;
        if (!it.hasNext()) {
            logger.error("No records in file, not even headers");
            success = false;
        } else {
            for (int i = 0; i < linesToIgnore; i++) {
                if (!it.hasNext()) {
                    success = false;
                    logger.error("Too few header lines in file, at zero-indexed line: %s", i);
                    break;
                } else {
                    it.next();
                }
            }
        }
        if (!it.hasNext()) {
            logger.error("No records in file after headers");
            success = false;
        }
        return success;
    }
}
