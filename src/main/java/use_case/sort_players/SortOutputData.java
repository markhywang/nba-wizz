package use_case.sort_players;

import java.util.List;

public class SortOutputData {

    private final List<String[]> sortedRows;
    private final int sortedColumnIndex;
    private final boolean ascending;

    public SortOutputData(List<String[]> sortedRows,
                          int sortedColumnIndex,
                          boolean ascending) {
        this.sortedRows = sortedRows;
        this.sortedColumnIndex = sortedColumnIndex;
        this.ascending = ascending;
    }

    public List<String[]> getSortedRows() {
        return sortedRows;
    }

    public int getSortedColumnIndex() {
        return sortedColumnIndex;
    }

    public boolean isAscending() {
        return ascending;
    }
}
