package interface_adapter.sort_players;

import use_case.sort.SortInputBoundary;
import use_case.sort.SortInputData;

import java.util.ArrayList;
import java.util.List;

public class SortController {

    private final SortInputBoundary interactor;
    private final SortViewModel viewModel;

    // column indices in the row array
    private static final int COL_POS = 1;
    private static final int COL_TEAM = 3;
    private static final int COL_SEASON = 4;

    public SortController(SortInputBoundary interactor, SortViewModel viewModel) {
        this.interactor = interactor;
        this.viewModel = viewModel;
    }

    // click column header to sort
    public void onColumnHeaderClicked(int columnIndex) {
        SortState state = viewModel.getState();
        List<String[]> currentRows = state.getTableData();

        if (currentRows == null || currentRows.isEmpty()) {
            return;
        }

        boolean ascending;
        if (state.getSortedColumnIndex() == columnIndex) {
            ascending = !state.isAscending();
        } else {
            ascending = false; // default descending
        }

        SortInputData inputData = new SortInputData(currentRows, columnIndex, ascending);
        interactor.execute(inputData);
    }

    // Filter button
    public void onFilterButtonClicked(String position, String team, String seasonFrom, String seasonTo) {
        SortState state = viewModel.getState();

        List<String[]> baseRows = state.getOriginalTableData();
        if (baseRows == null) {
            baseRows = state.getTableData();
        }
        if (baseRows == null) {
            return;
        }

        String trimmedPos = position == null ? "" : position.trim();
        String trimmedTeam = team == null ? "" : team.trim();
        String trimmedFrom = seasonFrom == null ? "" : seasonFrom.trim();
        String trimmedTo = seasonTo == null ? "" : seasonTo.trim();

        Integer fromYear = null;
        Integer toYear = null;

        try {
            if (!trimmedFrom.isEmpty()) {
                fromYear = Integer.parseInt(trimmedFrom);
            }
            if (!trimmedTo.isEmpty()) {
                toYear = Integer.parseInt(trimmedTo);
            }
        } catch (NumberFormatException e) {
            state.setErrorMessage("Invalid season range");
            viewModel.setState(state);
            viewModel.firePropertyChanged();
            return;
        }

        List<String[]> filtered = new ArrayList<>();
        for (String[] row : baseRows) {
            if (row == null) {
                continue;
            }

            // position filter
            if (!trimmedPos.isEmpty()) {
                String rowPos = getCell(row, COL_POS);
                if (!trimmedPos.equalsIgnoreCase(rowPos)) {
                    continue;
                }
            }

            // team filter
            if (!trimmedTeam.isEmpty()) {
                String rowTeam = getCell(row, COL_TEAM);
                if (!trimmedTeam.equalsIgnoreCase(rowTeam)) {
                    continue;
                }
            }

            // season range filter
            if (fromYear != null || toYear != null) {
                String seasonStr = getCell(row, COL_SEASON);
                try {
                    int season = Integer.parseInt(seasonStr);
                    if (fromYear != null && season < fromYear) {
                        continue;
                    }
                    if (toYear != null && season > toYear) {
                        continue;
                    }
                } catch (NumberFormatException ignored) {
                    continue;
                }
            }

            filtered.add(row);
        }

        state.setErrorMessage(null);
        state.setTableData(filtered);
        state.setSortedColumnIndex(-1);
        state.setAscending(false);

        viewModel.setState(state);
        viewModel.firePropertyChanged();
    }

    private String getCell(String[] row, int index) {
        if (row == null || index < 0 || index >= row.length) {
            return "";
        }
        return row[index] == null ? "" : row[index];
    }

    // Clear button
    public void onClearFilters() {
        SortState state = viewModel.getState();
        List<String[]> original = state.getOriginalTableData();
        if (original != null) {
            state.setTableData(original);
        }
        state.setSortedColumnIndex(-1);
        state.setAscending(false);
        state.setErrorMessage(null);

        viewModel.setState(state);
        viewModel.firePropertyChanged();
    }

    // Home button
    public void onHomeButtonClicked() {
        viewModel.getViewManagerModel().setActiveView("main_menu");
        viewModel.getViewManagerModel().firePropertyChanged();
    }

    // called at wiring time to give the full list
    public void setInitialTableData(List<String[]> rows) {
        SortState state = viewModel.getState();
        state.setOriginalTableData(rows);
        state.setTableData(rows);
        state.setSortedColumnIndex(-1);
        state.setAscending(false);
        state.setErrorMessage(null);

        viewModel.setState(state);
        viewModel.firePropertyChanged();
    }
}
