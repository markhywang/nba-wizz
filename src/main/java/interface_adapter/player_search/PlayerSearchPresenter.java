package interface_adapter.player_search;

import interface_adapter.ViewManagerModel;
import use_case.player_search.PlayerSearchOutputBoundary;
import use_case.player_search.PlayerSearchOutputData;

public class PlayerSearchPresenter implements PlayerSearchOutputBoundary {
    private final PlayerSearchViewModel playerSearchViewModel;
    private final ViewManagerModel viewManagerModel;

    public PlayerSearchPresenter(PlayerSearchViewModel playerSearchViewModel, ViewManagerModel viewManagerModel) {
        this.playerSearchViewModel = playerSearchViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void present(PlayerSearchOutputData response) {
        if (response.isUseCaseFailed()) {
            PlayerSearchState playerSearchState = playerSearchViewModel.getState();
            playerSearchState.setError("Player not found.");
            playerSearchViewModel.firePropertyChanged();
        } else {
            PlayerSearchState playerSearchState = playerSearchViewModel.getState();
            // In a real application, you would format the player data for the view.
            // For now, we'll just set a success message or update state as needed.
            playerSearchViewModel.firePropertyChanged();
            // Optionally, switch to a different view to display the results.
            // viewManagerModel.setActiveView("player_details");
            // viewManagerModel.firePropertyChanged();
        }
    }
}
