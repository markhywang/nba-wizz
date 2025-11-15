package use_case.generate_insights;

public interface GenerateInsightsOutputBoundary {
    void prepareSuccessView(GenerateInsightsOutputData outputData);
    void prepareFailView(String error);
}
