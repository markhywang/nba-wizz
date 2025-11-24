package interface_adapter.filter_players;

import use_case.filter_players.FilterPlayersOutputBoundary;
import use_case.filter_players.FilterPlayersOutputData;

public class FilterPlayersPresenter implements FilterPlayersOutputBoundary {
    private final FilterPlayersViewModel vm;

    public FilterPlayersPresenter(FilterPlayersViewModel vm) {
        this.vm = vm;
    }

    /** Helper: recompute visible rows + banner from state. */
    private void updatePageFromState() {
        FilterPlayersState state = vm.getState();
        int total = state.allRows.size();
        int pageSize = state.pageSize <= 0 ? 50 : state.pageSize;
        int maxPage = (total == 0) ? 0 : (total - 1) / pageSize;

        // clamp page
        if (state.currentPage < 0) state.currentPage = 0;
        if (state.currentPage > maxPage) state.currentPage = maxPage;

        if (total == 0) {
            state.tableRows = java.util.List.of();
            state.bannerMessage = "No players match your filters. Adjust filters and try again.";
            return;
        }

        int from = state.currentPage * pageSize;
        int to = Math.min(total, from + pageSize);
        state.tableRows = state.allRows.subList(from, to);

        int humanPage = state.currentPage + 1;
        int totalPages = maxPage + 1;
        state.bannerMessage = "Showing " + (from + 1) + "-" + to + " of "
                + total + " players (page " + humanPage + " of " + totalPages + ").";
    }

    @Override
    public void present(FilterPlayersOutputData out) {
        FilterPlayersState state = vm.getState();
        state.allRows = out.getRows();
        state.currentPage = 0; // reset to first page
        updatePageFromState();
        vm.firePropertyChanged();
    }

    @Override
    public void presentEmptyState(String message) {
        FilterPlayersState state = vm.getState();
        state.allRows.clear();
        state.tableRows.clear();
        state.bannerMessage = message;
        state.currentPage = 0;
        vm.firePropertyChanged();
    }

    @Override
    public void presentWarning(String message) {
        // Non-blocking: just show a message, keep current rows.
        FilterPlayersState state = vm.getState();
        state.bannerMessage = message;
        vm.firePropertyChanged();
    }

    @Override
    public void presentError(String message) {
        FilterPlayersState state = vm.getState();
        state.allRows.clear();
        state.tableRows.clear();
        state.bannerMessage = message;
        state.currentPage = 0;
        vm.firePropertyChanged();
    }

    @Override
    public void presentLargeResultNotice(FilterPlayersOutputData out, String summary) {
        // We no longer do pagination in the interactor, so treat this the same as present()
        FilterPlayersState state = vm.getState();
        state.allRows = out.getRows();
        state.currentPage = 0;
        updatePageFromState();
        // Append the summary to the banner if you want to keep the text:
        state.bannerMessage = summary;
        vm.firePropertyChanged();
    }

    @Override
    public void cleared() {
        FilterPlayersState state = vm.getState();
        state.selectedTeams.clear();
        state.selectedPositions.clear();
        state.seasonMin = java.util.Optional.empty();
        state.seasonMax = java.util.Optional.empty();
        state.currentPage = 0;
        // rows & banner will be set by a following present(...)
    }
}
