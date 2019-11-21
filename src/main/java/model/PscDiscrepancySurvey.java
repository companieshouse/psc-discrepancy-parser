package model;

import java.util.Date;
import java.util.List;

/**
 * The details filled in by an ObligedEntity about discrepancies with a Person of Significant
 * Control (PSC) for a company. Most of the information is captured in a list of question-answer
 * pairs, stored in {@link #questionsAndAnswers}.
 */
public class PscDiscrepancySurvey {
    private PscDiscrepancySurveyObligedEntity obligedEntity;
    private Date discrepancyIdentifiedOn;
    private String companyName;
    private String companyNumber;
    private String discrepancyType;

    private List<PscDiscrepancySurveyQandA> questionsAndAnswers;

    public PscDiscrepancySurveyObligedEntity getObligedEntity() {
        return obligedEntity;
    }

    public void setObligedEntity(PscDiscrepancySurveyObligedEntity obligedEntity) {
        this.obligedEntity = obligedEntity;
    }

    public Date getDiscrepancyIdentifiedOn() {
        return discrepancyIdentifiedOn;
    }

    public void setDiscrepancyIdentifiedOn(Date discrepancyIdentifiedOn) {
        this.discrepancyIdentifiedOn = discrepancyIdentifiedOn;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public List<PscDiscrepancySurveyQandA> getQuestionsAndAnswers() {
        return questionsAndAnswers;
    }

    public void setQuestionsAndAnswers(List<PscDiscrepancySurveyQandA> questionsAndAnswers) {
        this.questionsAndAnswers = questionsAndAnswers;
    }

    public String getDiscrepancyType() {
        return discrepancyType;
    }

    public void setDiscrepancyType(String discrepancyType) {
        this.discrepancyType = discrepancyType;
    }

    @Override
    public String toString() {
        return "PscDiscrepancy [obligedEntity=" + obligedEntity
                        + ", discrepancyIdentifiedOn=" + discrepancyIdentifiedOn
                        + ", discrepancyType=" + discrepancyType
                        + ", companyName=" + companyName
                        + ", companyNumber=" + companyNumber
                        + ", questionsAndAnswers=" + questionsAndAnswers + "]";
    }
}
