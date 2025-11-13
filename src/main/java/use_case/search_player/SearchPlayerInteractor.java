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

        Player player = playerDataAccessObject.getPlayerByName(inputData.getPlayerName());

        if (player == null) {
            presenter.presentPlayerNotFound("Player not found.");
            return;
        }

        int start = Integer.parseInt(inputData.getStartSeason());
        int end = Integer.parseInt(inputData.getEndSeason());

        List<String[]> tableRows = new ArrayList<>();
        Map<String, Map<Integer, Double>> graphData = new HashMap<>();

        for (String stat : inputData.getSelectedStats()) {
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

            if (inputData.getSelectedStats().contains("PPG"))
                graphData.get("PPG").put(stats.getSeasonYear(), stats.getPointsPerGame());
            if (inputData.getSelectedStats().contains("APG"))
                graphData.get("APG").put(stats.getSeasonYear(), stats.getAssistsPerGame());
            if (inputData.getSelectedStats().contains("RPG"))
                graphData.get("RPG").put(stats.getSeasonYear(), stats.getReboundsPerGame());
            if (inputData.getSelectedStats().contains("FG"))
                graphData.get("FG").put(stats.getSeasonYear(), stats.getFieldGoalPercentage());
        }

        presenter.present(new SearchPlayerOutputData(tableRows, graphData));
    }
}


