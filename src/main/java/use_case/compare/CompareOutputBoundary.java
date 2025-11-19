package use_case.compare;

public interface CompareOutputBoundary {
    void present(CompareOutputData outputData);
    void presentError(String message);
    void switchToMainMenu();
}