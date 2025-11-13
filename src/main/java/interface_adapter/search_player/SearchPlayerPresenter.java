package interface_adapter.search_player;

import use_case.search_player.SearchPlayerOutputBoundary;
import use_case.search_player.SearchPlayerOutputData;

public class SearchPlayerPresenter implements SearchPlayerOutputBoundary {
    private final SearchPlayerViewModel viewModel;

    public SearchPlayerPresenter(SearchPlayerViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(SearchPlayerOutputData outputData) {
        SearchPlayerState state = viewModel.getState();

        state.setErrorMessage(null);

        state.setResultsTableData(outputData.getTableRows());

        state.setGraphData(outputData.getGraphData());

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
