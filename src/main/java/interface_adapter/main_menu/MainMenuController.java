package interface_adapter.main_menu;

import interface_adapter.ViewManagerModel;
import use_case.main_menu.MainMenuInputBoundary;
import use_case.main_menu.MainMenuInputData;

public class MainMenuController {
    private final MainMenuInputBoundary interactor;
    private final ViewManagerModel viewManagerModel;

    public MainMenuController(MainMenuInputBoundary interactor,
                              ViewManagerModel viewManagerModel) {
        this.interactor = interactor;
        this.viewManagerModel = viewManagerModel;
    }

    public void onSearchPlayerPressed() {
        interactor.switchToSearchPlayer();
    }

    public void switchToGenerateInsights() {
        interactor.switchToGenerateInsights();
    }

    public void switchToChat() {
        interactor.switchToChat();
    }

    public void onCompareButtonClicked() {
        viewManagerModel.setActiveView("compare");
        viewManagerModel.firePropertyChanged();
    }

    public void onFilterAndSortButtonClicked() {
        viewManagerModel.setActiveView("sort_players");
        viewManagerModel.firePropertyChanged();
    }

}
