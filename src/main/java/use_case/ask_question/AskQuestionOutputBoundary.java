package use_case.ask_question;

public interface AskQuestionOutputBoundary {
    void prepareSuccessView(AskQuestionOutputData outputData);
    void prepareFailView(String error);
}
