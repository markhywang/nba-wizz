package use_case.ask_question;

public class AskQuestionInputData {
    private final String question;

    public AskQuestionInputData(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }
}
