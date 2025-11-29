package data_access;

import entity.Player;
import entity.SeasonStats;
import entity.Team;
import entity.Normalization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CsvPlayerDataAccessObject implements PlayerDataAccessInterface {
    private static final Logger log = LoggerFactory.getLogger(CsvPlayerDataAccessObject.class);
    private final String csvFile;
    private final List<Player> players = new ArrayList<>();
    private final Map<String, Player> playerMap = new HashMap<>();
    private int nextPlayerId = 1;

    public CsvPlayerDataAccessObject(String csvFile) {
        this.csvFile = csvFile;
        load();
    }

    private void load() {
        String line;
        String cvsSplitBy = ",";

        try (InputStream inputStream = getClass().getResourceAsStream("/data/" + csvFile)) {
            assert inputStream != null;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                br.readLine(); // Skip header
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(cvsSplitBy);

                    try {
                        String name = data[0].toLowerCase();
                        String pos = data[1];
                        int age = Integer.parseInt(data[2]);
                        String teamName = data[3];
                        int season = Integer.parseInt(data[4]);
                        int gamesPlayed = Integer.parseInt(data[5]);
                        double minutesPlayed = Double.parseDouble(data[6]);
                        double fgPercentage = Double.parseDouble(data[7]);
                        double threePtPercentage = Double.parseDouble(data[8]);
//                        double ftPercentage = Double.parseDouble(data[9]);
                        double trb = Double.parseDouble(data[10]);
                        double ast = Double.parseDouble(data[11]);
//                        double tov = Double.parseDouble(data[12]);
//                        double stl = Double.parseDouble(data[13]);
//                        double blk = Double.parseDouble(data[14]);
//                        double pf = Double.parseDouble(data[15]);
                        double pts = Double.parseDouble(data[16]);

                        Player player = playerMap.get(name);
                        if (player == null) {
                            // Create a dummy team for now.
                            Team team = new Team(0, teamName, "N/A", new ArrayList<>(), 0, 0, "N/A", new HashMap<>());
                            player = new Player(nextPlayerId++, name, team, pos, age, 0, 0, new ArrayList<>());
                            playerMap.put(name, player);
                            players.add(player);
                        }

                        SeasonStats seasonStats = new SeasonStats(season, pts, ast, trb, fgPercentage, gamesPlayed, minutesPlayed, threePtPercentage, player);
                        player.getCareerStats().add(seasonStats);

                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                        System.out.println("Skipping malformed line: " + line);
                    }
                }
            }
        } catch (IOException | NullPointerException e) {
            log.error(String.valueOf(e));
        }
    }

    @Override
    public void save(Player entity) {
        players.add(entity);
        // In a real application, you would write to the CSV file.
    }

    @Override
    public Optional<Player> findById(int id) {
        return players.stream().filter(player -> player.getPlayerID() == id).findFirst();
    }

    @Override
    public List<Player> findAll() {
        return new ArrayList<>(players);
    }

    @Override
    public void update(Player entity) {
        // Find the player and update their details.
    }

    @Override
    public void delete(Player entity) {
        players.remove(entity);
    }

    @Override
    public Player getPlayerByName(String playerName) {
        return playerMap.get(playerName.toLowerCase());
    }

    // @Override
    // public Map<String, Double> getAggregatedMetrics(String playerName, int seasonStartInclusive, int seasonEndInclusive, Normalization normalization, List<String> metrics) {
        // return Map.of();
    // }

    @Override
    public Map<String, Double> getAggregatedMetrics(
            String playerName,
            int seasonStartInclusive,
            int seasonEndInclusive,
            Normalization normalization,
            List<String> metrics) {

        Map<String, Double> sums = new HashMap<>();
        Map<String, Double> counts = new HashMap<>();
        for (String metric : metrics) {
            sums.put(metric, 0.0);
            counts.put(metric, 0.0);
        }

        String cvsSplitBy = ",";
        String line;

        try (InputStream inputStream = getClass().getResourceAsStream("/data/" + csvFile);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)))) {

            // skip header line
            bufferedReader.readLine();

            // column indices in CSV:
            final int NAME_IDX = 0;
            final int SEASON_IDX = 4;
            final int MP_IDX = 6;
            final int FG_IDX = 7;
            final int THREE_IDX = 8;
            final int FT_IDX = 9;
            final int TRB_IDX = 10;
            final int AST_IDX = 11;
            final int TOV_IDX = 12;
            final int STL_IDX = 13;
            final int BLK_IDX = 14;
            final int PF_IDX = 15;
            final int PTS_IDX = 16;

            // map metric names to column index
            Map<String, Integer> metricIndex = new HashMap<>();
            metricIndex.put("PTS", PTS_IDX);
            metricIndex.put("TRB", TRB_IDX);
            metricIndex.put("AST", AST_IDX);
            metricIndex.put("STL", STL_IDX);
            metricIndex.put("BLK", BLK_IDX);
            metricIndex.put("PF", PF_IDX);
            metricIndex.put("TOV", TOV_IDX);
            metricIndex.put("3P%", THREE_IDX);
            metricIndex.put("FT%", FT_IDX);
            metricIndex.put("FG%", FG_IDX);

            while ((line = bufferedReader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] data = line.split(cvsSplitBy);
                if (data.length <= PTS_IDX) {
                    continue;
                }

                String name = data[NAME_IDX].trim();
                if (!name.equalsIgnoreCase(playerName)) {
                    continue;
                }

                // season filter
                int season;
                try {
                    season = Integer.parseInt(data[SEASON_IDX].trim());
                } catch (NumberFormatException e) {
                    continue;
                }
                if (season < seasonStartInclusive || season > seasonEndInclusive) {
                    continue;
                }

                // MP used for PER_36
                double mp = 0.0;
                try {
                    mp = Double.parseDouble(data[MP_IDX].trim());
                } catch (NumberFormatException ignored) {
                }

                // add each requested metric
                for (String metric : metrics) {
                    Integer colIndex = metricIndex.get(metric);
                    if (colIndex == null) {
                        continue;
                    }
                    String raw = data[colIndex].trim();
                    if (raw.isBlank()) {
                        continue;
                    }

                    double value;
                    try {
                        value = Double.parseDouble(raw);
                    } catch (NumberFormatException e) {
                        continue;
                    }

                    // PER_36 for non-percentage stats
                    if (normalization == Normalization.PER_36 && !metric.endsWith("%")) {
                        if (mp <= 0) {
                            continue;
                        }
                        value = value * 36.0 / mp;
                    }

                    sums.put(metric, sums.get(metric) + value);
                    counts.put(metric, counts.get(metric) + 1);
                }
            }
        } catch (IOException | NullPointerException e) {
            log.error(String.valueOf(e));
            return Collections.emptyMap();
        }

        // turn sums into averages
        // turn sums into averages
        Map<String, Double> result = new HashMap<>();
        for (String metric : metrics) {
            Double count = counts.get(metric);
            if (count > 0) {
                result.put(metric, sums.get(metric) / count);
            }
        }
        return result;

    }
}
