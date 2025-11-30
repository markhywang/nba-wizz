package interface_adapter.sort_players;

import interface_adapter.ViewManagerModel;
import org.junit.jupiter.api.Test;
import use_case.sort_players.SortOutputData;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SortPresenterTest {

    @Test
    void presentUpdatesStateFromOutputData() {
        SortViewModel viewModel = new SortViewModel();
        ViewManagerModel manager = new ViewManagerModel();
        SortPresenter presenter = new SortPresenter(viewModel, manager);

        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"id", "PG", "LAL", "2020"});

        SortOutputData outputData = new SortOutputData(rows, 2, true);

        presenter.present(outputData);

        SortState state = viewModel.getState();
        assertNull(state.getErrorMessage());
        assertEquals(rows, state.getTableData());
        assertEquals(2, state.getSortedColumnIndex());
        assertTrue(state.isAscending());
    }

    @Test
    void presentNoPlayersSetsErrorAndClearsTable() {
        SortViewModel viewModel = new SortViewModel();
        ViewManagerModel manager = new ViewManagerModel();
        SortPresenter presenter = new SortPresenter(viewModel, manager);

        // put some initial data
        SortState initial = viewModel.getState();
        List<String[]> tableData = new ArrayList<>();
        tableData.add(new String[]{"some", "data"});
        initial.setTableData(tableData);
        viewModel.setState(initial);

        String message = "No players found";

        presenter.presentNoPlayers(message);

        SortState state = viewModel.getState();
        assertEquals(message, state.getErrorMessage());
        assertNull(state.getTableData());
        assertEquals(-1, state.getSortedColumnIndex());
        assertFalse(state.isAscending());
    }
}
