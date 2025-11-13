package use_case.search_player;

import java.util.List;

public class SearchPlayerInputData {
    private final String playerName;
    private final String startSeason;
    private final String endSeason;
    private final List<String> selectedStats;

    public SearchPlayerInputData(String playerName, String startSeason,
                                 String endSeason, List<String> selectedStats) {
        this.playerName = playerName;
        this.startSeason = startSeason;
        this.endSeason = endSeason;
        this.selectedStats = selectedStats;
    }

    public String getPlayerName() { return playerName; }
    public String getStartSeason() { return startSeason; }
    public String getEndSeason() { return endSeason; }
    public List<String> getSelectedStats() { return selectedStats; }
}


