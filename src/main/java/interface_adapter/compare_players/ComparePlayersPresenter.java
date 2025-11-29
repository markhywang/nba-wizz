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
        state.setComparison(outputData.getComparison().response());
        state.setLoading(false);
        comparePlayersViewModel.setState(state);
        comparePlayersViewModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String error) {
        ComparePlayersState state = comparePlayersViewModel.getState();
        state.setError(error);
        state.setLoading(false);
        comparePlayersViewModel.setState(state);
        comparePlayersViewModel.firePropertyChanged();
    }

    @Override
    public void prepareLoadingView() {
        ComparePlayersState state = comparePlayersViewModel.getState();
        state.setLoading(true);
        state.setComparison("");
        state.setError(null);
        comparePlayersViewModel.setState(state);
        comparePlayersViewModel.firePropertyChanged();
    }
}
