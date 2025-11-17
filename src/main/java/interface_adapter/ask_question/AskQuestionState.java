package interface_adapter.ask_question;

public class AskQuestionState {
    private String question = "";
    private String answer = "";
    private String error = null;
    private boolean isLoading = false;

    public AskQuestionState(AskQuestionState copy) {
        this.question = copy.question;
        this.answer = copy.answer;
        this.error = copy.error;
        this.isLoading = copy.isLoading;
    }

    public AskQuestionState() {}

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }
}
