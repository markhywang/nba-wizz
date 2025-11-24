package interface_adapter.filter_players;

import use_case.filter_players.FilterPlayersOutputBoundary;
import use_case.filter_players.FilterPlayersOutputData;

import java.util.Collections;
import java.util.Optional;

public class FilterPlayersPresenter implements FilterPlayersOutputBoundary {

    private final FilterPlayersViewModel viewModel;

    public FilterPlayersPresenter(FilterPlayersViewModel viewModel) {
        this.viewModel = viewModel;
    }

    private FilterPlayersState state() {
        return viewModel.getState();
    }

    @Override
    public void present(FilterPlayersOutputData outputData) {
        FilterPlayersState s = state();
        s.tableRows = outputData.getRows();
        s.bannerMessage = "";
        s.errorMessage = "";
        viewModel.firePropertyChanged();
    }

    @Override
    public void presentEmptyState(String message) {
        FilterPlayersState s = state();
        s.tableRows = Collections.emptyList();
        s.bannerMessage = message;
        s.errorMessage = "";
        viewModel.firePropertyChanged();
    }

    @Override
    public void presentWarning(String message) {
        FilterPlayersState s = state();
        s.bannerMessage = message;
        // Do not clear table; just update the banner.
        viewModel.firePropertyChanged();
    }

    @Override
    public void presentError(String message) {
        FilterPlayersState s = state();
        s.errorMessage = message;
        viewModel.firePropertyChanged();
    }

    @Override
    public void presentLargeResultNotice(FilterPlayersOutputData outputData, String summary) {
        FilterPlayersState s = state();
        s.tableRows = outputData.getRows();
        s.bannerMessage = summary;
        s.errorMessage = "";
        viewModel.firePropertyChanged();
    }

    @Override
    public void cleared() {
        FilterPlayersState s = state();
        s.selectedTeams.clear();
        s.selectedPositions.clear();
        s.seasonMin = Optional.empty();
        s.seasonMax = Optional.empty();
        s.tableRows = Collections.emptyList();
        s.bannerMessage = "";
        s.errorMessage = "";
        viewModel.firePropertyChanged();
    }
}
