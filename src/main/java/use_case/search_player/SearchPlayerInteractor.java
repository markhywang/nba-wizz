package use_case.search_player;

import data_access.PlayerDataAccessInterface;
import entity.Player;
import entity.SeasonStats;

import java.util.*;

public class SearchPlayerInteractor implements SearchPlayerInputBoundary {
    private final PlayerDataAccessInterface playerDataAccessObject;
    private final SearchPlayerOutputBoundary presenter;

    public SearchPlayerInteractor(PlayerDataAccessInterface playerDataAccessObject, SearchPlayerOutputBoundary
            presenter) {
        this.playerDataAccessObject = playerDataAccessObject;
        this.presenter = presenter;
    }

    @Override
    public void execute(SearchPlayerInputData inputData) {

        if (inputData.startSeason().isEmpty() || inputData.endSeason().isEmpty()) {
            presenter.presentPlayerNotFound("Season fields cannot be empty.");
            return;
        }

        String startStr = inputData.startSeason();
        String endStr   = inputData.endSeason();

        if (!startStr.matches("\\d+") || !endStr.matches("\\d+")) {
            presenter.presentPlayerNotFound("Season fields must be numbers only.");
            return;
        }

        int start = Integer.parseInt(inputData.startSeason());
        int end = Integer.parseInt(inputData.endSeason());

        Player player = playerDataAccessObject.getPlayerByName(inputData.playerName());

        if (start < 1980 || end > 2024 ) {
            presenter.presentPlayerNotFound("Season range must be between 1980 and 2024");
            return;
        }

        if (start > end) {
            presenter.presentPlayerNotFound("Start season cannot be after end season");
            return;
        }

        if (player == null) {
            presenter.presentPlayerNotFound("Player not found. Re-enter a valid player name.");
            return;
        }

        List<String[]> tableRows = new ArrayList<>();
        Map<String, Map<Integer, Double>> graphData = new HashMap<>();

        for (String stat : inputData.selectedStats()) {
            graphData.put(stat, new HashMap<>());
        }

        for (SeasonStats stats : player.getCareerStats()) {
            if (stats.getSeasonYear() < start || stats.getSeasonYear() > end) continue;

            String[] row = new String[] {
                    String.valueOf(stats.getSeasonYear()),
                    String.valueOf(stats.getPointsPerGame()),
                    String.valueOf(stats.getAssistsPerGame()),
                    String.valueOf(stats.getReboundsPerGame()),
                    String.valueOf(stats.getFieldGoalPercentage())
            };
            tableRows.add(row);

            if (inputData.selectedStats().contains("PPG"))
                graphData.get("PPG").put(stats.getSeasonYear(), stats.getPointsPerGame());
            if (inputData.selectedStats().contains("APG"))
                graphData.get("APG").put(stats.getSeasonYear(), stats.getAssistsPerGame());
            if (inputData.selectedStats().contains("RPG"))
                graphData.get("RPG").put(stats.getSeasonYear(), stats.getReboundsPerGame());
            if (inputData.selectedStats().contains("FG"))
                graphData.get("FG").put(stats.getSeasonYear(), stats.getFieldGoalPercentage());
        }

        presenter.present(new SearchPlayerOutputData(tableRows, graphData));
    }
}


