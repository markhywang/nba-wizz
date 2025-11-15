package use_case.main_menu;

public interface MainMenuOutputBoundary {
    void presentSearchPlayerView();

    void prepareSuccessView(MainMenuOutputData mainMenuOutputData);

    void prepareFailView(String error);

    void switchToGenerateInsights();
}
