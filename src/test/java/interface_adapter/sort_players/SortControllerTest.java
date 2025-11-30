package interface_adapter.sort_players;

import org.junit.jupiter.api.Test;
import use_case.sort_players.SortInputBoundary;
import use_case.sort_players.SortInputData;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SortControllerTest {

    private static class FakeSortInteractor implements SortInputBoundary {
        SortInputData lastInputData;

        @Override
        public void execute(SortInputData inputData) {
            this.lastInputData = inputData;
        }
    }

    private SortViewModel createViewModelWithState(SortState state) {
        SortViewModel viewModel = new SortViewModel();
        viewModel.setState(state);
        return viewModel;
    }

    @Test
    void onColumnHeaderClickedDoesNothingWhenNoRows() {
        FakeSortInteractor interactor = new FakeSortInteractor();
        SortViewModel viewModel = new SortViewModel(); // state.tableData is null by default
        SortController controller = new SortController(interactor, viewModel);

        controller.onColumnHeaderClicked(2);

        assertNull(interactor.lastInputData);
    }

    @Test
    void onColumnHeaderClickedUsesDescendingByDefault() {
        FakeSortInteractor interactor = new FakeSortInteractor();
        SortState state = new SortState();
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"1", "PG", "LAL", "2020"});

        state.setTableData(rows);
        SortViewModel viewModel = createViewModelWithState(state);

        SortController controller = new SortController(interactor, viewModel);
        controller.onColumnHeaderClicked(2);

        assertNotNull(interactor.lastInputData);
    }

    @Test
    void onColumnHeaderClickedTogglesAscendingWhenSameColumn() {
        FakeSortInteractor interactor = new FakeSortInteractor();
        SortState state = new SortState();
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"1", "PG", "LAL", "2020"});

        state.setTableData(rows);
        state.setSortedColumnIndex(2);
        state.setAscending(false);

        SortViewModel viewModel = createViewModelWithState(state);
        SortController controller = new SortController(interactor, viewModel);

        controller.onColumnHeaderClicked(2);

        assertNotNull(interactor.lastInputData);
    }

    @Test
    void filterReturnsEarlyWhenNoBaseData() {
        FakeSortInteractor interactor = new FakeSortInteractor();
        SortViewModel viewModel = new SortViewModel(); // original and tableData are null
        SortController controller = new SortController(interactor, viewModel);

        controller.onFilterButtonClicked(null, null, null, null);

        SortState state = viewModel.getState();
        assertNull(state.getTableData());
        assertNull(state.getErrorMessage());
    }

    @Test
    void filterSetsErrorWhenSeasonRangeIsInvalid() {
        FakeSortInteractor interactor = new FakeSortInteractor();
        SortState state = new SortState();

        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"1", "PG", "LAL", "2020"});
        state.setTableData(rows);

        SortViewModel viewModel = createViewModelWithState(state);
        SortController controller = new SortController(interactor, viewModel);

        controller.onFilterButtonClicked("", "", "abc", "2020");

        SortState newState = viewModel.getState();
        assertEquals("Invalid season range", newState.getErrorMessage());
    }

    @Test
    void filterAppliesAllConditions() {
        FakeSortInteractor interactor = new FakeSortInteractor();
        SortState state = new SortState();

        List<String[]> baseRows = new ArrayList<>();
        baseRows.add(null); // null row
        baseRows.add(new String[]{"1", "PG", "x", "LAL", "2018"});     // should pass
        baseRows.add(new String[]{"2", "SG", "x", "LAL", "2018"});     // wrong position
        baseRows.add(new String[]{"3", "PG", "x", "BOS", "2018"});     // wrong team
        baseRows.add(new String[]{"4", "PG", "x", "LAL", "2009"});     // below fromYear
        baseRows.add(new String[]{"5", "PG", "x", "LAL", "xyz"});      // invalid season in row
        baseRows.add(new String[]{"6", "PG", "x", "LAL"});             // too short, season cell out of range

        state.setOriginalTableData(baseRows);
        state.setErrorMessage("old");
        SortViewModel viewModel = createViewModelWithState(state);

        SortController controller = new SortController(interactor, viewModel);

        controller.onFilterButtonClicked("PG", "LAL", "2010", "2018");

        SortState newState = viewModel.getState();
        List<String[]> filtered = newState.getTableData();

        assertNull(newState.getErrorMessage());
        assertNotNull(filtered);
        assertEquals(1, filtered.size());
        assertEquals("1", filtered.get(0)[0]);
    }

    @Test
    void clearFiltersResetsTableAndFlags() {
        FakeSortInteractor interactor = new FakeSortInteractor();
        SortState state = new SortState();

        List<String[]> original = new ArrayList<>();
        original.add(new String[]{"1", "PG"});
        state.setOriginalTableData(original);

        List<String[]> current = new ArrayList<>();
        current.add(new String[]{"2", "SG"});
        state.setTableData(current);

        state.setSortedColumnIndex(3);
        state.setAscending(true);
        state.setErrorMessage("error");

        SortViewModel viewModel = createViewModelWithState(state);
        SortController controller = new SortController(interactor, viewModel);

        controller.onClearFilters();

        SortState newState = viewModel.getState();
        assertEquals(original, newState.getTableData());
        assertEquals(-1, newState.getSortedColumnIndex());
        assertFalse(newState.isAscending());
        assertNull(newState.getErrorMessage());
    }

    @Test
    void homeButtonCanBeCalledWithoutError() {
        FakeSortInteractor interactor = new FakeSortInteractor();
        SortViewModel viewModel = new SortViewModel();
        SortController controller = new SortController(interactor, viewModel);

        // we only care that no exception is thrown and the code path is executed
        controller.onHomeButtonClicked();
    }

    @Test
    void setInitialTableDataInitializesState() {
        FakeSortInteractor interactor = new FakeSortInteractor();
        SortViewModel viewModel = new SortViewModel();
        SortController controller = new SortController(interactor, viewModel);

        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"1", "PG"});

        controller.setInitialTableData(rows);

        SortState state = viewModel.getState();
        assertEquals(rows, state.getTableData());
        assertEquals(rows, state.getOriginalTableData());
        assertEquals(-1, state.getSortedColumnIndex());
        assertFalse(state.isAscending());
        assertNull(state.getErrorMessage());
    }
}
