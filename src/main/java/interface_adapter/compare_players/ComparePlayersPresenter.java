package interface_adapter.compare_players;

import interface_adapter.ViewManagerModel;
import use_case.compare_players.ComparePlayersOutputBoundary;
import use_case.compare_players.ComparePlayersOutputData;

public class ComparePlayersPresenter implements ComparePlayersOutputBoundary {
    private final ComparePlayersViewModel comparePlayersViewModel;
    private final ViewManagerModel viewManagerModel;

    public ComparePlayersPresenter(ComparePlayersViewModel comparePlayersViewModel, ViewManagerModel viewManagerModel) {
        this.comparePlayersViewModel = comparePlayersViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void prepareSuccessView(ComparePlayersOutputData outputData) {
        ComparePlayersState state = comparePlayersViewModel.getState();
        state.setComparison(outputData.getComparison().getResponse());
        comparePlayersViewModel.setState(state);
        comparePlayersViewModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String error) {
        ComparePlayersState state = comparePlayersViewModel.getState();
        state.setError(error);
        comparePlayersViewModel.setState(state);
        comparePlayersViewModel.firePropertyChanged();
    }
}
