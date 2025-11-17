package interface_adapter.filter_players;

import java.util.*;

public class FilterPlayersState {
    public Set<String> selectedTeams = new HashSet<>();
    public Set<String> selectedPositions = new HashSet<>();
    public Optional<Integer> seasonMin = Optional.empty();
    public Optional<Integer> seasonMax = Optional.empty();

    public List<String[]> tableRows = new ArrayList<>();
    public String bannerMessage = "";   // warnings / large result notices / errors
}