package use_case.main_menu;

import data_access.PlayerDataAccessInterface;

public class MainMenuInteractor implements MainMenuInputBoundary {
    private final PlayerDataAccessInterface playerDataAccess;
    private final MainMenuOutputBoundary mainMenuPresenter;

    public MainMenuInteractor(PlayerDataAccessInterface playerDataAccess, MainMenuOutputBoundary mainMenuPresenter) {
        this.playerDataAccess = playerDataAccess;
        this.mainMenuPresenter = mainMenuPresenter;
    }

    @Override
    public void execute(MainMenuInputData mainMenuInputData) {
        // TODO: Implement the main menu logic
    }

    @Override
    public void switchToGenerateInsights() {
        mainMenuPresenter.prepareSuccessView(new MainMenuOutputData());
    }
}
