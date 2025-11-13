package use_case.main_menu;

public interface MainMenuOutputBoundary {
    void prepareSuccessView(MainMenuOutputData outputData);
    void prepareFailView(String error);
}
