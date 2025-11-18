package interface_adapter.sort_players;

import java.util.List;

public class SortState {

    private String errorMessage;

    // currently displayed rows (after filter + sort)
    private List<String[]> tableData;

    // original full list (used for filtering / clear)
    private List<String[]> originalTableData;

    private int sortedColumnIndex = -1;

    private boolean ascending = false;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<String[]> getTableData() {
        return tableData;
    }

    public void setTableData(List<String[]> tableData) {
        this.tableData = tableData;
    }

    public List<String[]> getOriginalTableData() {
        return originalTableData;
    }

    public void setOriginalTableData(List<String[]> originalTableData) {
        this.originalTableData = originalTableData;
    }

    public int getSortedColumnIndex() {
        return sortedColumnIndex;
    }

    public void setSortedColumnIndex(int sortedColumnIndex) {
        this.sortedColumnIndex = sortedColumnIndex;
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }
}
