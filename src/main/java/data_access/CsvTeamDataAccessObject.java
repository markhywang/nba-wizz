package data_access;

import entity.Team;
import entity.Normalization;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Team DAO built from the same player CSV.
 * For each team, we compute the average per-game (or per-36) stats
 * over all players on that team in the given seasons.
 *
 * This doesn't try to be perfectly realistic NBA team stats;
 * it's just good enough for the CSC207 project.
 */
public class CsvTeamDataAccessObject implements TeamDataAccessInterface {

    private final String csvFile;
    private final List<Team> teams = new ArrayList<>();
    private final Map<String, Team> teamMap = new HashMap<>();
    private int nextTeamId = 1;

    public CsvTeamDataAccessObject(String csvFile) {
        this.csvFile = csvFile;
        loadTeamsFromCsv();
    }

    public CsvTeamDataAccessObject() {
        this.csvFile = "";
        loadTeamsFromCsv();
    }

    private void loadTeamsFromCsv() {
        // Very simple: we only create Team objects with ids + names.
        String line;
        String split = ",";

        try (InputStream inputStream =
                     getClass().getResourceAsStream("/data/" + csvFile);
             BufferedReader br =
                     new BufferedReader(new InputStreamReader(inputStream))) {

            br.readLine(); // skip header

            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] data = line.split(split);
                if (data.length < 4) continue;

                String teamName = data[3].trim().toLowerCase(); // Team column

                if (!teamMap.containsKey(teamName)) {
                    Team team = new Team(nextTeamId++, teamName,
                            "N/A", new ArrayList<>(), 0, 0, "N/A", new HashMap<>());
                    teamMap.put(teamName, team);
                    teams.add(team);
                }
            }

        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    // ---------- DataAccessInterface<Team> methods ----------

    @Override
    public void save(Team entity) {
        teams.add(entity);
        teamMap.put(entity.getName(), entity);
    }

    @Override
    public Optional<Team> findById(int id) {
        return teams.stream().filter(t -> t.getTeamID() == id).findFirst();
    }

    @Override
    public List<Team> findAll() {
        return new ArrayList<>(teams);
    }

    @Override
    public void update(Team entity) {
        // Simple in-memory: nothing fancy needed for this project.
    }

    @Override
    public void delete(Team entity) {
        teams.remove(entity);
        teamMap.remove(entity.getName());
    }

    @Override
    public Team getTeamByName(String teamName) {
        if (teamMap == null) return null;
        return teamMap.get(teamName.trim().toLowerCase());
    }

    // ---------- TeamDataAccessInterface methods ----------

    @Override
    public List<Team> findByConference(String conference) {
        // If your Team entity tracks conference properly, filter here.
        // For now, just return all; you can refine later if needed.
        return new ArrayList<>(teams);
    }

    @Override
    public Map<String, Double> getAggregatedMetrics(
            String teamName,
            int seasonStartInclusive,
            int seasonEndInclusive,
            Normalization normalization,
            List<String> metrics) {

        if(teamName != null) {
            teamName = teamName.trim().toUpperCase();
        }

        Map<String, Double> sums = new HashMap<>();
        Map<String, Integer> counts = new HashMap<>();
        for (String m : metrics) {
            sums.put(m, 0.0);
            counts.put(m, 0);
        }

        String split = ",";
        String line;

        try (InputStream inputStream =
                     getClass().getResourceAsStream("/data/" + csvFile);
             BufferedReader br =
                     new BufferedReader(new InputStreamReader(inputStream))) {

            br.readLine(); // skip header

            // Column indices in the player CSV
            final int TEAM_IDX   = 3;
            final int SEASON_IDX = 4;
            final int MP_IDX     = 6;
            final int FG_IDX     = 7;
            final int THREE_IDX  = 8;
            final int FT_IDX     = 9;
            final int TRB_IDX    = 10;
            final int AST_IDX    = 11;
            final int TOV_IDX    = 12;
            final int STL_IDX    = 13;
            final int BLK_IDX    = 14;
            final int PF_IDX     = 15;
            final int PTS_IDX    = 16;

            Map<String, Integer> metricIndex = new HashMap<>();
            metricIndex.put("PTS", PTS_IDX);
            metricIndex.put("TRB", TRB_IDX);
            metricIndex.put("AST", AST_IDX);
            metricIndex.put("STL", STL_IDX);
            metricIndex.put("BLK", BLK_IDX);
            metricIndex.put("PF",  PF_IDX);
            metricIndex.put("TOV", TOV_IDX);
            metricIndex.put("FG%", FG_IDX);
            metricIndex.put("3P%", THREE_IDX);
            metricIndex.put("FT%", FT_IDX);

            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] data = line.split(split);
                if (data.length <= PTS_IDX) continue;

                String team = data[TEAM_IDX].trim();
                if (!team.equals(teamName)) continue;

                int season;
                try {
                    season = Integer.parseInt(data[SEASON_IDX].trim());
                } catch (NumberFormatException e) {
                    continue;
                }
                if (season < seasonStartInclusive || season > seasonEndInclusive)
                    continue;

                double mp = 0.0;
                try {
                    mp = Double.parseDouble(data[MP_IDX].trim());
                } catch (NumberFormatException ignored) { }

                for (String metric : metrics) {
                    Integer col = metricIndex.get(metric);
                    if (col == null) continue;

                    String raw = data[col].trim();
                    if (raw.isEmpty()) continue;

                    double value;
                    try {
                        value = Double.parseDouble(raw);
                    } catch (NumberFormatException e) {
                        continue;
                    }

                    if (normalization == Normalization.PER_36 && !metric.endsWith("%")) {
                        if (mp <= 0) continue;
                        value = value * 36.0 / mp;
                    }

                    sums.put(metric, sums.get(metric) + value);
                    counts.put(metric, counts.get(metric) + 1);
                }
            }

        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }

        Map<String, Double> result = new HashMap<>();
        for (String metric : metrics) {
            int c = counts.get(metric);
            if (c > 0) {
                result.put(metric, sums.get(metric) / c);
            }
        }
        return result;
    }
}
