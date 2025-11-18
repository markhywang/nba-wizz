package interface_adapter.filter_players;

import use_case.filter_players.FilterPlayersOutputBoundary;
import use_case.filter_players.FilterPlayersOutputData;

public class FilterPlayersPresenter implements FilterPlayersOutputBoundary {
    private final FilterPlayersViewModel vm;

    public FilterPlayersPresenter(FilterPlayersViewModel vm) {
        this.vm = vm;
    }

    @Override
    public void present(FilterPlayersOutputData out) {
        vm.getState().tableRows = out.getRows();
        vm.getState().bannerMessage = "";
        vm.firePropertyChanged();
    }

    @Override
    public void presentEmptyState(String message) {
        vm.getState().tableRows = java.util.List.of();
        vm.getState().bannerMessage = message;
        vm.firePropertyChanged();
    }

    @Override
    public void presentWarning(String message) {
        vm.getState().bannerMessage = message; // non-blocking
        vm.firePropertyChanged();
    }

    @Override
    public void presentError(String message) {
        vm.getState().bannerMessage = message;
        vm.firePropertyChanged();
    }

    @Override
    public void presentLargeResultNotice(FilterPlayersOutputData out, String summary) {
        vm.getState().tableRows = out.getRows();
        vm.getState().bannerMessage = summary;
        vm.firePropertyChanged();
    }

    @Override
    public void cleared() {
        vm.getState().selectedTeams.clear();
        vm.getState().selectedPositions.clear();
        vm.getState().seasonMin = java.util.Optional.empty();
        vm.getState().seasonMax = java.util.Optional.empty();
    }
}