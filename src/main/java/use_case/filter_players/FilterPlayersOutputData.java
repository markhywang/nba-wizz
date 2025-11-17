package use_case.filter_players;

import java.util.List;

public class FilterPlayersOutputData {
    // Table-friendly rows: [Name, Team, Pos, Seasons]
    private final List<String[]> rows;
    public FilterPlayersOutputData(List<String[]> rows) { this.rows = rows; }
    public List<String[]> getRows() { return rows; }
}