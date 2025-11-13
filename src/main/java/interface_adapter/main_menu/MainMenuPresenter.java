package interface_adapter.main_menu;

import interface_adapter.ViewManagerModel;
import interface_adapter.generate_insights.GenerateInsightsViewModel;
import use_case.main_menu.MainMenuOutputBoundary;
import use_case.main_menu.MainMenuOutputData;

public class MainMenuPresenter implements MainMenuOutputBoundary {
    private final MainMenuViewModel mainMenuViewModel;
    private final ViewManagerModel viewManagerModel;
    private final GenerateInsightsViewModel generateInsightsViewModel;

    public MainMenuPresenter(MainMenuViewModel mainMenuViewModel, ViewManagerModel viewManagerModel, GenerateInsightsViewModel generateInsightsViewModel) {
        this.mainMenuViewModel = mainMenuViewModel;
        this.viewManagerModel = viewManagerModel;
        this.generateInsightsViewModel = generateInsightsViewModel;
    }

    @Override
    public void prepareSuccessView(MainMenuOutputData outputData) {
        // On success, switch to the generate insights view.
        viewManagerModel.setActiveView(generateInsightsViewModel.getViewName());
        viewManagerModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String error) {
        MainMenuState mainMenuState = mainMenuViewModel.getState();
        mainMenuState.setError(error);
        mainMenuViewModel.firePropertyChanged();
    }
}
