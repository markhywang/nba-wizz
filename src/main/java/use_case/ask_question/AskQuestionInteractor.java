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

        presenter.presentLoading();

        // Run the API call in a background thread to avoid blocking the UI
        new Thread(() -> {
            try {
                String context = dataAccess.getDatasetContent();
                String fullAnswer = dataAccess.getAnswerSync(inputData.getQuestion(), context);
                Answer answer = new Answer(fullAnswer);
                AskQuestionOutputData outputData = new AskQuestionOutputData(answer, inputData.getQuestion(), false);
                presenter.prepareSuccessView(outputData);
            } catch (IOException e) {
                presenter.prepareFailView("Error getting answer: " + e.getMessage());
            }
        }).start();
    }
}
