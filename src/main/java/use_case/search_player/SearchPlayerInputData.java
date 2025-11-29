package use_case.search_player;

import java.util.List;

public record SearchPlayerInputData(String playerName, String startSeason, String endSeason,
                                    List<String> selectedStats) {
}


