package use_case.search_player;

import java.util.List;
import java.util.Map;

public record SearchPlayerOutputData(List<String[]> tableRows, Map<String, Map<Integer, Double>> graphData) {
}

