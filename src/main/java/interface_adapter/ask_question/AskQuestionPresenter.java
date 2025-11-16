package interface_adapter.ask_question;

import interface_adapter.ViewManagerModel;
import use_case.ask_question.AskQuestionOutputBoundary;
import use_case.ask_question.AskQuestionOutputData;

public class AskQuestionPresenter implements AskQuestionOutputBoundary {
    private final AskQuestionViewModel askQuestionViewModel;
    private final ViewManagerModel viewManagerModel;

    public AskQuestionPresenter(AskQuestionViewModel askQuestionViewModel, ViewManagerModel viewManagerModel) {
        this.askQuestionViewModel = askQuestionViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void prepareSuccessView(AskQuestionOutputData outputData) {
        AskQuestionState state = askQuestionViewModel.getState();
        state.setAnswer(outputData.getAnswer().getResponse());
        state.setQuestion(outputData.getQuestion());
        askQuestionViewModel.setState(state);
        askQuestionViewModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String error) {
        AskQuestionState state = askQuestionViewModel.getState();
        state.setError(error);
        askQuestionViewModel.setState(state);
        askQuestionViewModel.firePropertyChanged();
    }
}
