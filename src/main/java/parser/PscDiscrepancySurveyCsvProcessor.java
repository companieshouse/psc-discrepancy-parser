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

/**
 * Parses the supplied CSV record(s) (assuming their format to be specific to a PSC Discrepancy
 * Survey), transforming the CSV to JSON, each of which is then supplied to
 * PscDiscrepancyFoundListener.
 */
public class PscDiscrepancySurveyCsvProcessor {
    private static final int INDEX_OF_OBLIGED_ENTITY_COMPANY_NAME = 0;
    private static final int INDEX_OF_OBLIGED_ENTITY_TYPE = 1;
    private static final int INDEX_OF_OBLIGED_ENTITY_CONTACT_NAME = 3;
    private static final int INDEX_OF_OBLIGED_ENTITY_CONTACT_EMAIL = 4;
    private static final int INDEX_OF_OBLIGED_ENTITY_CONTACT_PHONE = 5;
    private static final int INDEX_OF_OBLIGED_ENTITY_CONTACT_ADDRESS_1 = 6;
    private static final int INDEX_OF_OBLIGED_ENTITY_CONTACT_ADDRESS_2 = 7;
    private static final int INDEX_OF_OBLIGED_ENTITY_CONTACT_ADDRESS_3 = 8;
    private static final int INDEX_OF_OBLIGED_ENTITY_CONTACT_ADDRESS_4 = 9;
    private static final int INDEX_OF_OBLIGED_ENTITY_CONTACT_ADDRESS_5 = 10;
    private static final int INDEX_OF_OBLIGED_ENTITY_CONTACT_ADDRESS_6 = 11;
    private static final int INDEX_OF_OBLIGED_ENTITY_CONTACT_ADDRESS_POSTCODE = 12;

    private static final int INDEX_OF_DISCREPANCY_IDENTIFIED_ON = 2;
    private static final int INDEX_OF_DISCREPANCY_COMPANY_NAME = 13;
    private static final int INDEX_OF_DISCREPANCY_COMPANY_NUMBER = 14;

    private static final int INDEX_OF_DISCREPANCY_TYPE = 15;

    private static final int INITIAL_LINES_TO_IGNORE = 3;
    private static final String NULL_FIELD = "-";
    private static final int CORRECT_COLUMN_COUNT = 100;
    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final Logger LOG = LogManager.getLogger(PscDiscrepancySurveyCsvProcessor.class);

    private final Reader reader;
    private final PscDiscrepancyFoundListener listener;
    private boolean successfullyProcessedSoFar = true;
    private int currentRecordBeingParsed = -1;

    public interface PscDiscrepancyFoundListener {
        boolean parsed(PscDiscrepancySurvey discrepancy);
    }

    public PscDiscrepancySurveyCsvProcessor(Reader reader, PscDiscrepancyFoundListener listener) {
        this.reader = reader;
        this.listener = listener;
    }

    public PscDiscrepancySurveyCsvProcessor(byte[] bytesToParse,
                    PscDiscrepancyFoundListener listener) {
        this.listener = listener;
        ByteArrayInputStream decodedBase64AsStream = new ByteArrayInputStream(bytesToParse);
        reader = new InputStreamReader(decodedBase64AsStream);
    }

    public boolean parseRecords() throws IOException {
        Iterator<CSVRecord> it = null;
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withNullString(NULL_FIELD).parse(reader);
        it = records.iterator();
        try {
            if (moveToStartOfData(it, INITIAL_LINES_TO_IGNORE)) {
                while (it.hasNext()) {
                    currentRecordBeingParsed++;
                    parseRecord(it.next());
                }
            } else {
                successfullyProcessedSoFar = false;
            }
        } catch (IllegalStateException ex) {
            LOG.error("Error parsing, could be corrupt CSV, at record number ([{}]: {}",
                            currentRecordBeingParsed, ex, ex);
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
            if (!listenerCallbackSuccess) {
                successfullyProcessedSoFar = false;
            }
        } else {
            successfullyProcessedSoFar = false;
        }
    }

    boolean parseObligedEntity(CSVRecord record, PscDiscrepancySurvey discrepancy) {
        PscDiscrepancySurveyObligedEntity oe = new PscDiscrepancySurveyObligedEntity();
        oe.setCompanyName(record.get(INDEX_OF_OBLIGED_ENTITY_COMPANY_NAME));
        oe.setObligedEntityType(record.get(INDEX_OF_OBLIGED_ENTITY_TYPE));
        oe.setContactName(record.get(INDEX_OF_OBLIGED_ENTITY_CONTACT_NAME));
        oe.setContactEmail(record.get(INDEX_OF_OBLIGED_ENTITY_CONTACT_EMAIL));
        oe.setContactPhone(record.get(INDEX_OF_OBLIGED_ENTITY_CONTACT_PHONE));
        oe.setContactAddressLine1(record.get(INDEX_OF_OBLIGED_ENTITY_CONTACT_ADDRESS_1));
        oe.setContactAddressLine2(record.get(INDEX_OF_OBLIGED_ENTITY_CONTACT_ADDRESS_2));
        oe.setContactAddressLine3(record.get(INDEX_OF_OBLIGED_ENTITY_CONTACT_ADDRESS_3));
        oe.setContactAddressLine4(record.get(INDEX_OF_OBLIGED_ENTITY_CONTACT_ADDRESS_4));
        oe.setContactAddressLine5(record.get(INDEX_OF_OBLIGED_ENTITY_CONTACT_ADDRESS_5));
        oe.setContactAddressLine6(record.get(INDEX_OF_OBLIGED_ENTITY_CONTACT_ADDRESS_6));
        oe.setContactAddressPostcode(record.get(INDEX_OF_OBLIGED_ENTITY_CONTACT_ADDRESS_POSTCODE));
        discrepancy.setObligedEntity(oe);
        return true;
    }

    boolean parseDiscrepancyBasicDetails(CSVRecord record, PscDiscrepancySurvey discrepancy) {
        discrepancy.setCompanyName(record.get(INDEX_OF_DISCREPANCY_COMPANY_NAME));
        discrepancy.setCompanyNumber(record.get(INDEX_OF_DISCREPANCY_COMPANY_NUMBER));

        discrepancy.setDiscrepancyType(record.get(INDEX_OF_DISCREPANCY_TYPE));

        String discrepancyIdentifiedOnStr = record.get(INDEX_OF_DISCREPANCY_IDENTIFIED_ON);
        if (discrepancyIdentifiedOnStr != null) {
            SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
            try {
                Date discrepancyIdentifiedOn = format.parse(discrepancyIdentifiedOnStr);
                discrepancy.setDiscrepancyIdentifiedOn(discrepancyIdentifiedOn);
            } catch (ParseException ex) {
                LOG.error("Could not parse discrepancyIdentifiedOnStr: {}",
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
                    LOG.error("Could not find question on zero-indexed record number: {}, zero-indexed column number: {}",
                                    currentRecordBeingParsed, i);
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

    boolean checkColumnCount(CSVRecord record) {
        if (CORRECT_COLUMN_COUNT != record.size()) {
            LOG.error("Unexpected number of columns in CSV record: {}", record.size());
            return false;
        }
        return true;
    }

    boolean moveToStartOfData(Iterator<CSVRecord> it, int linesToIgnore) {
        boolean success = true;
        if (!it.hasNext()) {
            LOG.error("No records in file, not even headers");
            success = false;
        } else {
            for (int i = 0; i < linesToIgnore; i++) {
                if (!it.hasNext()) {
                    success = false;
                    LOG.error("Too few header lines in file, at zero-indexed line: {}", i);
                    break;
                } else {
                    it.next();
                }
            }
            if (!it.hasNext()) {
                LOG.error("No records in file after headers");
                success = false;
            }
        }
        return success;
    }
}
