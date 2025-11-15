package interface_adapter.search_player;

import java.util.List;
import java.util.Map;

public class SearchPlayerState {

    private String errorMessage;

    private List<String[]> resultsTableData;

    private Map<String, Map<Integer, Double>> graphData;

    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<String[]> getResultsTableData() {
        return resultsTableData;
    }
    public void setResultsTableData(List<String[]> resultsTableData) {
        this.resultsTableData = resultsTableData;
    }

    public Map<String, Map<Integer, Double>> getGraphData() {
        return graphData;
    }
    public void setGraphData(Map<String, Map<Integer, Double>> graphData) {
        this.graphData = graphData;
    }
}