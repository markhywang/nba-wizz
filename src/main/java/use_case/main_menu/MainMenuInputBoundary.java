package use_case.main_menu;

public interface MainMenuInputBoundary {
    void switchToSearchPlayer();

    void execute(MainMenuInputData mainMenuInputData);

    void switchToGenerateInsights();
}
