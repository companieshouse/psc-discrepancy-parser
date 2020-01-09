package parser;

import parser.CsvProcessor.CsvProcessorListener;

public class CsvProcessorFactory {
    public CsvProcessor createPscDiscrepancySurveyCsvProcessor(
                    byte[] extractedCsv, CsvProcessorListener listener) {
        return new CsvProcessor(extractedCsv, listener);
    }
}
