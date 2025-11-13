package use_case.search_player;

import java.util.List;
import java.util.Map;

import entity.Player;

public class SearchPlayerOutputData {
    private final List<String[]> tableRows;

    private final Map<String, Map<Integer, Double>> graphData;

    public SearchPlayerOutputData(List<String[]> tableRows,
                                  Map<String, Map<Integer, Double>> graphData) {
        this.tableRows = tableRows;
        this.graphData = graphData;
    }

    public List<String[]> getTableRows() {
        return tableRows;
    }

    public Map<String, Map<Integer, Double>> getGraphData() {
        return graphData;
    }
}

