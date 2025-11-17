package use_case.filter_players;

public interface FilterPlayersInputBoundary {
    void execute(FilterPlayersInputData inputData);
    void clear();
}
