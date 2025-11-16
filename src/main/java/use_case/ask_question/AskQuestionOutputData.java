package use_case.ask_question;

import entity.Answer;

public class AskQuestionOutputData {
    private final Answer answer;
    private final String question;
    private final boolean useCaseFailed;

    public AskQuestionOutputData(Answer answer, String question, boolean useCaseFailed) {
        this.answer = answer;
        this.question = question;
        this.useCaseFailed = useCaseFailed;
    }

    public Answer getAnswer() {
        return answer;
    }

    public String getQuestion() {
        return question;
    }
}
