package use_case.main_menu;

public interface MainMenuOutputBoundary {
    void onSearchPlayerSuccess(MainMenuOutputData outputData);
    void onSearchPlayerFailure(String message);
    void switchToSearchPlayer();
    void switchToGenerateInsights();
    void switchView(String viewName);
}
