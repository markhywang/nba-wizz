package interface_adapter.sort_players;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SortStateTest {

    @Test
    void gettersAndSettersWork() {
        SortState state = new SortState();

        // default values
        assertNull(state.getErrorMessage());
        assertNull(state.getTableData());
        assertNull(state.getOriginalTableData());
        assertEquals(-1, state.getSortedColumnIndex());
        assertFalse(state.isAscending());

        // error message
        state.setErrorMessage("some error");
        assertEquals("some error", state.getErrorMessage());

        // table data
        List<String[]> tableData = new ArrayList<>();
        tableData.add(new String[]{"row1"});
        state.setTableData(tableData);
        assertEquals(tableData, state.getTableData());

        // original table data
        List<String[]> original = new ArrayList<>();
        original.add(new String[]{"orig"});
        state.setOriginalTableData(original);
        assertEquals(original, state.getOriginalTableData());

        // sorted column index
        state.setSortedColumnIndex(3);
        assertEquals(3, state.getSortedColumnIndex());

        // ascending flag
        state.setAscending(true);
        assertTrue(state.isAscending());
    }
}
