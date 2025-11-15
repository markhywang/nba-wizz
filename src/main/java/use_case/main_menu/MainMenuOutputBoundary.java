package use_case.main_menu;

public interface MainMenuOutputBoundary {
    void switchToSearchPlayer();

    void prepareSuccessView(MainMenuOutputData mainMenuOutputData);

    void prepareFailView(String error);

    void switchToGenerateInsights();
}
