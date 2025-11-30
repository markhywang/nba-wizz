package use_case.sort_players;

import java.util.List;

public record SortInputData(List<String[]> tableRows, int columnIndex, boolean ascending) {

}
