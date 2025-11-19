package use_case.compare_players;

public interface ComparePlayersOutputBoundary {
    void prepareSuccessView(ComparePlayersOutputData outputData);
    void prepareFailView(String error);
    void prepareLoadingView();
}
