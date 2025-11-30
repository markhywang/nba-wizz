package interface_adapter.search_player;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SearchPlayerStateTest {

    @Test
    void gettersAndSettersWork() {
        SearchPlayerState state = new SearchPlayerState();

        assertNull(state.getErrorMessage());
        assertNull(state.getResultsTableData());
        assertNull(state.getGraphData());

        String error = "error";
        state.setErrorMessage(error);
        assertEquals(error, state.getErrorMessage());

        List<String[]> tableData = new ArrayList<>();
        tableData.add(new String[]{"a", "b"});
        state.setResultsTableData(tableData);
        assertEquals(tableData, state.getResultsTableData());

        Map<String, Map<Integer, Double>> graphData = new HashMap<>();
        Map<Integer, Double> inner = new HashMap<>();
        inner.put(2020, 1.0);
        graphData.put("PTS", inner);
        state.setGraphData(graphData);
        assertEquals(graphData, state.getGraphData());
    }
}
