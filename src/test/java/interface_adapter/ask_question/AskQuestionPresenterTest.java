package interface_adapter.ask_question;

import entity.Answer;
import interface_adapter.ViewManagerModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import use_case.ask_question.AskQuestionOutputData;

import static org.junit.jupiter.api.Assertions.*;

class AskQuestionPresenterTest {

    private AskQuestionViewModel viewModel;
    private ViewManagerModel viewManagerModel;
    private AskQuestionPresenter presenter;

    @BeforeEach
    void setUp() {
        viewModel = new AskQuestionViewModel();
        viewManagerModel = new ViewManagerModel();
        presenter = new AskQuestionPresenter(viewModel, viewManagerModel);
    }

    @Test
    void prepareSuccessView_updatesViewModelStateCorrectly() {
        Answer answer = new Answer("This is the answer.");
        AskQuestionOutputData outputData = new AskQuestionOutputData(answer, "The question?", false);

        presenter.prepareSuccessView(outputData);

        AskQuestionState state = viewModel.getState();
        assertEquals("This is the answer.", state.getAnswer());
        assertEquals("The question?", state.getQuestion());
        assertNull(state.getError());
    }

    @Test
    void prepareFailView_updatesViewModelStateWithError() {
        presenter.prepareFailView("An error occurred.");

        AskQuestionState state = viewModel.getState();
        assertEquals("An error occurred.", state.getError());
        assertEquals("", state.getAnswer());
    }
}
