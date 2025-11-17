package use_case.filter_players;

public interface FilterPlayersOutputBoundary {
    void present(FilterPlayersOutputData outputData);
    void presentEmptyState(String message);        // “No players match…”
    void presentWarning(String message);           // non-blocking warning
    void presentError(String message);             // hard error
    void presentLargeResultNotice(FilterPlayersOutputData outputData, String summary);
    void cleared();                                // after Clear Filters
}
