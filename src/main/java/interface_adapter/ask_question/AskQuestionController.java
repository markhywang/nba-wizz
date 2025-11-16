package interface_adapter.ask_question;

import use_case.ask_question.AskQuestionInputBoundary;
import use_case.ask_question.AskQuestionInputData;

public class AskQuestionController {
    private final AskQuestionInputBoundary askQuestionInteractor;

    public AskQuestionController(AskQuestionInputBoundary askQuestionInteractor) {
        this.askQuestionInteractor = askQuestionInteractor;
    }

    public void execute(String question) {
        AskQuestionInputData inputData = new AskQuestionInputData(question);
        askQuestionInteractor.execute(inputData);
    }
}
