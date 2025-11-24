package interface_adapter.filter_players;

import java.util.*;

public class FilterPlayersState {
    public Set<String> selectedTeams = new HashSet<>();
    public Set<String> selectedPositions = new HashSet<>();
    public Optional<Integer> seasonMin = Optional.empty();
    public Optional<Integer> seasonMax = Optional.empty();

    /** All rows that match the current filters (for pagination). */
    public List<String[]> allRows = new ArrayList<>();

    /** Rows currently visible in the table (one page). */
    public List<String[]> tableRows = new ArrayList<>();

    /** 0-based page index. */
    public int currentPage = 0;

    /** Page size (number of rows per page). */
    public int pageSize = 50;

    /** Banner / info / warning text shown above or below the table. */
    public String bannerMessage = "";
}
