package handler;

import parser.PscDiscrepancySurveyCsvProcessor;

public class PscDiscrepancySurveyCsvProcessorFactory {
    public PscDiscrepancySurveyCsvProcessor createPscDiscrepancySurveyCsvProcessor(
                    byte[] extractedCsv, PscDiscrepancyFoundListenerImpl listener) {
        return new PscDiscrepancySurveyCsvProcessor(extractedCsv, listener);
    }
}
