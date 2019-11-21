package model;

/**
 * A question and answer pair. The questions do not individually vary, so are represented by an
 * enum. The variable part is in the answer, which will always be a String.
 */
public class PscDiscrepancySurveyQandA {
    @Override
    public String toString() {
        return "QA: [q=" + question.name() + ", a=" + answer + "]";
    }
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
}
