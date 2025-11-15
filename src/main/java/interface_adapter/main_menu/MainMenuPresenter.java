package interface_adapter.main_menu;

import interface_adapter.ViewManagerModel;
import use_case.main_menu.MainMenuOutputBoundary;
import use_case.main_menu.MainMenuOutputData;

public class MainMenuPresenter implements MainMenuOutputBoundary {
    private final ViewManagerModel viewManagerModel;

    public MainMenuPresenter(ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void presentSearchPlayerView() {
        viewManagerModel.setActiveView("search_player");
        viewManagerModel.firePropertyChanged();
    }
}
