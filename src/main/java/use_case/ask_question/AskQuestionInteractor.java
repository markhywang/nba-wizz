package use_case.ask_question;

import entity.Answer;

import java.io.IOException;

public class AskQuestionInteractor implements AskQuestionInputBoundary {
    private final AskQuestionDataAccessInterface dataAccess;
    private final AskQuestionOutputBoundary presenter;

    public AskQuestionInteractor(AskQuestionDataAccessInterface dataAccess, AskQuestionOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(AskQuestionInputData inputData) {
        if (inputData.getQuestion() == null || inputData.getQuestion().trim().isEmpty()) {
            presenter.prepareFailView("Question cannot be empty.");
            return;
        }

        try {
            String context = dataAccess.getDatasetContent();
            String answerText = dataAccess.getAnswer(inputData.getQuestion(), context);

            if (answerText.startsWith("Error:") || answerText.contains("I cannot answer this question.")) {
                presenter.prepareFailView(answerText);
            } else {
                Answer answer = new Answer(answerText);
                AskQuestionOutputData outputData = new AskQuestionOutputData(answer, inputData.getQuestion(), false);
                presenter.prepareSuccessView(outputData);
            }
        } catch (IOException e) {
            presenter.prepareFailView("Error reading dataset: " + e.getMessage());
        } catch (Exception e) {
            presenter.prepareFailView("An unexpected error occurred: " + e.getMessage());
        }
    }
}
