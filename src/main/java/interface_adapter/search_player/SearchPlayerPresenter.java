package interface_adapter.search_player;

import interface_adapter.ViewManagerModel;
import use_case.search_player.SearchPlayerOutputBoundary;
import use_case.search_player.SearchPlayerOutputData;

public class SearchPlayerPresenter implements SearchPlayerOutputBoundary {
    private final SearchPlayerViewModel viewModel;
    public final ViewManagerModel viewManagerModel;

    public SearchPlayerPresenter(SearchPlayerViewModel viewModel, ViewManagerModel viewManagerModel) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void present(SearchPlayerOutputData outputData) {
        SearchPlayerState state = viewModel.getState();

        state.setErrorMessage(null);

        state.setResultsTableData(outputData.tableRows());

        state.setGraphData(outputData.graphData());

        viewModel.setState(state);

    }

    @Override
    public void presentPlayerNotFound(String message) {
        SearchPlayerState state = new SearchPlayerState();

        state.setErrorMessage(message);
        state.setResultsTableData(null);
        state.setGraphData(null);

        viewModel.setState(state);
    }
}
