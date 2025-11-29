package use_case.sort_players;

import java.util.List;

public record SortOutputData(List<String[]> sortedRows, int sortedColumnIndex, boolean ascending) {

}
