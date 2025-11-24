package interface_adapter.filter_players;

import java.util.*;

/**
 * Mutable state for the Filter Players feature.
 * The view reads and writes these fields through the ViewModel.
 */
public class FilterPlayersState {

    // Currently selected filters
    public Set<String> selectedTeams = new HashSet<>();
    public Set<String> selectedPositions = new HashSet<>();
    public Optional<Integer> seasonMin = Optional.empty();
    public Optional<Integer> seasonMax = Optional.empty();

    // Table rows to display. One String[] per row.
    public List<String[]> tableRows = new ArrayList<>();

    // Messages shown above or near the table.
    public String bannerMessage = "";
    public String errorMessage = "";

    public FilterPlayersState() {
    }

    public FilterPlayersState(FilterPlayersState copy) {
        this.selectedTeams = new HashSet<>(copy.selectedTeams);
        this.selectedPositions = new HashSet<>(copy.selectedPositions);
        this.seasonMin = copy.seasonMin;
        this.seasonMax = copy.seasonMax;
        this.tableRows = new ArrayList<>(copy.tableRows);
        this.bannerMessage = copy.bannerMessage;
        this.errorMessage = copy.errorMessage;
    }
}
