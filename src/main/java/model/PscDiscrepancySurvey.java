package model;

import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    private String requestId;

    private List<PscDiscrepancySurveyQandA> questionsAndAnswers;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

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
        return "PscDiscrepancy [requestId=" + requestId
                        + ", obligedEntity=" + obligedEntity
                        + ", discrepancyIdentifiedOn=" + discrepancyIdentifiedOn
                        + ", discrepancyType=" + discrepancyType
                        + ", companyName=" + companyName
                        + ", companyNumber=" + companyNumber
                        + ", questionsAndAnswers=" + questionsAndAnswers + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyName, companyNumber, discrepancyIdentifiedOn, discrepancyType,
                        obligedEntity, questionsAndAnswers);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof PscDiscrepancySurvey)) {
            return false;
        }
        PscDiscrepancySurvey other = (PscDiscrepancySurvey) obj;
        return Objects.equals(companyName, other.companyName)
                        && Objects.equals(companyNumber, other.companyNumber)
                        && Objects.equals(discrepancyIdentifiedOn, other.discrepancyIdentifiedOn)
                        && Objects.equals(discrepancyType, other.discrepancyType)
                        && Objects.equals(obligedEntity, other.obligedEntity)
                        && Objects.equals(questionsAndAnswers, other.questionsAndAnswers);
    }
}
