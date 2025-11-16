package interface_adapter.compare_players;

import entity.Answer;
import interface_adapter.ViewManagerModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import use_case.compare_players.ComparePlayersOutputData;

import static org.junit.jupiter.api.Assertions.*;

class ComparePlayersPresenterTest {

    private ComparePlayersViewModel viewModel;
    private ViewManagerModel viewManagerModel;
    private ComparePlayersPresenter presenter;

    @BeforeEach
    void setUp() {
        viewModel = new ComparePlayersViewModel();
        viewManagerModel = new ViewManagerModel();
        presenter = new ComparePlayersPresenter(viewModel, viewManagerModel);
    }

    @Test
    void prepareSuccessView_updatesViewModelStateCorrectly() {
        Answer comparison = new Answer("Player 1 is better.");
        ComparePlayersOutputData outputData = new ComparePlayersOutputData(comparison, false);

        presenter.prepareSuccessView(outputData);

        ComparePlayersState state = viewModel.getState();
        assertEquals("Player 1 is better.", state.getComparison());
        assertNull(state.getError());
    }

    @Test
    void prepareFailView_updatesViewModelStateWithError() {
        presenter.prepareFailView("Player not found.");

        ComparePlayersState state = viewModel.getState();
        assertEquals("Player not found.", state.getError());
        assertEquals("", state.getComparison());
    }
}
