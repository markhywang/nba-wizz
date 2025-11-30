package interface_adapter.sort_players;

import interface_adapter.ViewManagerModel;
import use_case.sort_players.SortOutputBoundary;
import use_case.sort_players.SortOutputData;


public class SortPresenter implements SortOutputBoundary {

    private final SortViewModel viewModel;
    private final ViewManagerModel viewManagerModel;

    public SortPresenter(SortViewModel viewModel, ViewManagerModel viewManagerModel) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void present(SortOutputData outputData) {
        SortState state = viewModel.getState();

        state.setErrorMessage(null);
        state.setTableData(outputData.sortedRows());
        state.setSortedColumnIndex(outputData.sortedColumnIndex());
        state.setAscending(outputData.ascending());

        viewModel.setState(state);
        viewModel.firePropertyChanged();
    }

    @Override
    public void presentNoPlayers(String message) {
        SortState state = new SortState();

        state.setErrorMessage(message);
        state.setTableData(null);
        state.setSortedColumnIndex(-1);
        state.setAscending(false);

        viewModel.setState(state);
        viewModel.firePropertyChanged();
    }
}
