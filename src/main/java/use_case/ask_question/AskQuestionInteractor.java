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

        try {
            String context = dataAccess.getDatasetContent();
            dataAccess.getAnswer(
                    inputData.getQuestion(),
                    context,
                    (String partialResponse) -> {
                        Answer answer = new Answer(partialResponse);
                        AskQuestionOutputData outputData = new AskQuestionOutputData(answer, inputData.getQuestion(), false);
                        presenter.presentPartialResponse(outputData);
                    },
                    () -> {
                        presenter.presentStreamingComplete();
                    },
                    (Exception e) -> {
                        presenter.prepareFailView(e.getMessage());
                    }
            );
        } catch (IOException e) {
            presenter.prepareFailView("Error reading dataset: " + e.getMessage());
        }
    }
}
