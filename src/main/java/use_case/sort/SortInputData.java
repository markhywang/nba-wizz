package use_case.sort;

import java.util.List;

public class SortInputData {

    private final List<String[]> tableRows;
    private final int columnIndex;
    private final boolean ascending;

    public SortInputData(List<String[]> tableRows, int columnIndex, boolean ascending) {
        this.tableRows = tableRows;
        this.columnIndex = columnIndex;
        this.ascending = ascending;
    }

    public List<String[]> getTableRows() {
        return tableRows;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public boolean isAscending() {
        return ascending;
    }
}
