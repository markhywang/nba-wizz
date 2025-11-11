package interface_adapter.main_menu;

import interface_adapter.ViewManagerModel;
import use_case.main_menu.MainMenuOutputBoundary;
import use_case.main_menu.MainMenuOutputData;

public class MainMenuPresenter implements MainMenuOutputBoundary {
    private final MainMenuViewModel mainMenuViewModel;
    private final ViewManagerModel viewManagerModel;

    public MainMenuPresenter(MainMenuViewModel mainMenuViewModel, ViewManagerModel viewManagerModel) {
        this.mainMenuViewModel = mainMenuViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void present(MainMenuOutputData response) {
        if (response.isUseCaseFailed()) {
            MainMenuState mainMenuState = mainMenuViewModel.getState();
            mainMenuState.setError("Player not found.");
            mainMenuViewModel.firePropertyChanged();
        } else {
            MainMenuState mainMenuState = mainMenuViewModel.getState();
            // In a real application, you would format the player data for the view.
            // For now, we'll just set a success message or update state as needed.
            mainMenuViewModel.firePropertyChanged();
            // Optionally, switch to a different view to display the results.
            // viewManagerModel.setActiveView("player_details");
            // viewManagerModel.firePropertyChanged();
        }
    }
}
