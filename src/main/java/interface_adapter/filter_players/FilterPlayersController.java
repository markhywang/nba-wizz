package interface_adapter.filter_players;

import use_case.filter_players.FilterPlayersInputBoundary;
import use_case.filter_players.FilterPlayersInputData;

import java.util.Optional;
import java.util.Set;

public class FilterPlayersController {
    private final FilterPlayersInputBoundary interactor;

    public FilterPlayersController(FilterPlayersInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void apply(Set<String> teams, Set<String> positions,
                      Optional<Integer> seasonMin, Optional<Integer> seasonMax) {
        interactor.execute(new FilterPlayersInputData(teams, positions, seasonMin, seasonMax));
    }

    public void clear() {
        interactor.clear();
    }
}