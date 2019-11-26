package model;

import java.util.Objects;

/**
 * A question and answer pair. The questions do not individually vary, so are represented by an
 * enum. The variable part is in the answer, which will always be a String.
 */
public class PscDiscrepancySurveyQandA {
    private PscDiscrepancySurveyQuestion question;
    private String answer;

    public PscDiscrepancySurveyQuestion getQuestion() {
        return question;
    }

    public void setQuestion(PscDiscrepancySurveyQuestion question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public int hashCode() {
        return Objects.hash(answer, question);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof PscDiscrepancySurveyQandA)) {
            return false;
        }
        PscDiscrepancySurveyQandA other = (PscDiscrepancySurveyQandA) obj;
        return Objects.equals(answer, other.answer) && question == other.question;
    }

    @Override
    public String toString() {
        return "QA: [q=" + question.name() + ", a=" + answer + "]";
    }
}
