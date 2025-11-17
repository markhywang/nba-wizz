package interface_adapter.sort_players;

import use_case.sort.SortInputBoundary;
import use_case.sort.SortInputData;

import java.util.List;


public class SortController {

    private final SortInputBoundary interactor;
    private final SortViewModel viewModel;

    public SortController(SortInputBoundary interactor, SortViewModel viewModel) {
        this.interactor = interactor;
        this.viewModel = viewModel;
    }


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

            ascending = false;
        }

        SortInputData inputData = new SortInputData(currentRows, columnIndex, ascending);
        interactor.execute(inputData);
    }


    public void setInitialTableData(List<String[]> rows) {
        SortState state = viewModel.getState();
        state.setTableData(rows);
        state.setSortedColumnIndex(-1);
        state.setAscending(false);
        state.setErrorMessage(null);

        viewModel.setState(state);
        viewModel.firePropertyChanged();
    }
}
