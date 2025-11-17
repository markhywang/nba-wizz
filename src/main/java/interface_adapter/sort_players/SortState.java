package interface_adapter.sort_players;

import java.util.List;


public class SortState {

    private String errorMessage;

    private List<String[]> tableData;

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
