package parser;

import parser.PscDiscrepancySurveyCsvProcessor.PscDiscrepancyCreatedListener;

public class PscDiscrepancySurveyCsvProcessorFactory {
    public PscDiscrepancySurveyCsvProcessor createPscDiscrepancySurveyCsvProcessor(
                    byte[] extractedCsv, PscDiscrepancyCreatedListener listener) {
        return new PscDiscrepancySurveyCsvProcessor(extractedCsv, listener);
    }
}
