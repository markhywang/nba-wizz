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
        state.setLoading(false); // Mark loading as complete
        askQuestionViewModel.setState(state);
        askQuestionViewModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String error) {
        AskQuestionState state = askQuestionViewModel.getState();
        state.setError(error);
        state.setLoading(false);
        askQuestionViewModel.setState(state);
        askQuestionViewModel.firePropertyChanged();
    }

    @Override
    public void presentLoading() {
        AskQuestionState state = askQuestionViewModel.getState();
        state.setLoading(true);
        state.setAnswer("");
        askQuestionViewModel.setState(state);
        askQuestionViewModel.firePropertyChanged();
    }

    @Override
    public void presentPartialResponse(AskQuestionOutputData outputData) {
        AskQuestionState state = askQuestionViewModel.getState();
        String currentAnswer = state.getAnswer() != null ? state.getAnswer() : "";
        state.setAnswer(currentAnswer + outputData.getAnswer().getResponse());
        askQuestionViewModel.setState(state);
        askQuestionViewModel.firePropertyChanged();
    }

    @Override
    public void presentStreamingComplete() {
        AskQuestionState state = askQuestionViewModel.getState();
        state.setLoading(false);
        askQuestionViewModel.setState(state);
        askQuestionViewModel.firePropertyChanged();
    }
}
