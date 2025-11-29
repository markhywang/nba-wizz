package interface_adapter.compare;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class CompareStateTest {

    @Test
    public void defaultConstructor_initializesEmptyState() {
        CompareState state = new CompareState();

        assertTrue(state.entities.isEmpty());
        assertEquals("", state.seasonLabel);
        assertTrue(state.table.isEmpty());
        assertTrue(state.notices.isEmpty());
        assertNull(state.insight);
        assertNull(state.error);
    }

    @Test
    public void rowVm_storesValuesCorrectly() {
        List<String> cells = List.of("25", "20.5");
        CompareState.RowVM row = new CompareState.RowVM("PTS", cells, 0);

        assertEquals("PTS", row.metric());
        assertEquals(cells, row.cells());
        assertEquals(Integer.valueOf(0), row.bestIndex());
    }
}
