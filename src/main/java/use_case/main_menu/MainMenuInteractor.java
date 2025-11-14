package use_case.main_menu;

import data_access.PlayerDataAccessInterface;

public class MainMenuInteractor implements MainMenuInputBoundary {
    private final MainMenuOutputBoundary mainMenuPresenter;

    public MainMenuInteractor(MainMenuOutputBoundary
            mainMenuPresenter) {this.mainMenuPresenter = mainMenuPresenter;}

    @Override
    public void switchToSearchPlayer() {
        mainMenuPresenter.presentSearchPlayerView();
    }
}
