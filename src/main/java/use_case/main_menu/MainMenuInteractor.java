package use_case.main_menu;

import data_access.PlayerDataAccessInterface;

public class MainMenuInteractor implements MainMenuInputBoundary {
    final PlayerDataAccessInterface playerDataAccessObject;
    private final MainMenuOutputBoundary mainMenuPresenter;

    public MainMenuInteractor(PlayerDataAccessInterface playerDataAccessObject, MainMenuOutputBoundary
            mainMenuPresenter) {
        this.playerDataAccessObject = playerDataAccessObject;
        this.mainMenuPresenter = mainMenuPresenter;}

    @Override
    public void switchToSearchPlayer() {
        mainMenuPresenter.switchToSearchPlayer();
    }

    @Override
    public void execute(MainMenuInputData mainMenuInputData) {
        // TODO: Implement this method
    }

    @Override
    public void switchToGenerateInsights() {
        mainMenuPresenter.switchToGenerateInsights();
    }
}
