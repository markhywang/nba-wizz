package use_case.sort;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SortInteractor implements SortInputBoundary {

    private final SortOutputBoundary presenter;

    public SortInteractor(SortOutputBoundary presenter) {
        this.presenter = presenter;
    }

    @Override
    public void execute(SortInputData inputData) {
        List<String[]> rows = inputData.getTableRows();

        if (rows == null || rows.isEmpty()) {
            presenter.presentNoPlayers("No players to sort.");
            return;
        }

        int columnIndex = inputData.getColumnIndex();
        boolean ascending = inputData.isAscending();

        List<String[]> sortedRows = new ArrayList<>(rows);

        Comparator<String[]> comparator = createComparator(columnIndex, ascending);
        sortedRows.sort(comparator);

        presenter.present(new SortOutputData(sortedRows, columnIndex, ascending));
    }

    private Comparator<String[]> createComparator(final int columnIndex, final boolean ascending) {

        return (row1, row2) -> {
            String v1 = getColumnValue(row1, columnIndex);
            String v2 = getColumnValue(row2, columnIndex);

            boolean missing1 = isMissing(v1);
            boolean missing2 = isMissing(v2);

            // Handle missing values first
            if (missing1 && missing2) {
                return 0;
            } else if (missing1 != missing2) {
                if (ascending) {
                    return missing1 ? -1 : 1;
                } else {
                    return missing1 ? 1 : -1;
                }
            }

            Double d1 = tryParseDouble(v1);
            Double d2 = tryParseDouble(v2);

            int result;
            if (d1 != null && d2 != null) {
                result = d1.compareTo(d2);
            } else {
                result = v1.compareToIgnoreCase(v2);
            }

            return ascending ? result : -result;
        };
    }

    private String getColumnValue(String[] row, int index) {
        if (row == null || index < 0 || index >= row.length) {
            return null;
        }
        return row[index];
    }


    private boolean isMissing(String value) {
        if (value == null) {
            return true;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return true;
        }
        String lower = trimmed.toLowerCase();
        return lower.equals("n/a") || lower.equals("na") || lower.equals("null");
    }


    private Double tryParseDouble(String value) {
        try {
            return Double.parseDouble(value.trim());
        } catch (Exception e) {
            return null;
        }
    }
}
