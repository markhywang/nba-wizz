package use_case.filter_players;

import java.util.Set;
import java.util.Optional;

public class FilterPlayersInputData {
    private final Set<String> teams;
    private final Set<String> positions;
    private final Optional<Integer> seasonMin;
    private final Optional<Integer> seasonMax;

    public FilterPlayersInputData(Set<String> teams, Set<String> positions,
                                  Optional<Integer> seasonMin, Optional<Integer> seasonMax) {
        this.teams = teams;
        this.positions = positions;
        this.seasonMin = seasonMin;
        this.seasonMax = seasonMax;
    }
    public Set<String> getTeams() { return teams; }
    public Set<String> getPositions() { return positions; }
    public Optional<Integer> getSeasonMin() { return seasonMin; }
    public Optional<Integer> getSeasonMax() { return seasonMax; }
}
