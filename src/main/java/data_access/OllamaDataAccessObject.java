package data_access;

import entity.Player;
import entity.SeasonStats;
import entity.Team;
import data_access.GenerateInsightsDataAccessInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OllamaDataAccessObject implements GenerateInsightsDataAccessInterface {
    private final String csvFile = "PlayerStatsDataset.csv";
    private final Map<String, Player> playerMap = new HashMap<>();
    private final Map<String, Team> teamMap = new HashMap<>();
    private int nextPlayerId = 1;
    private int nextTeamId = 1;
    public OllamaDataAccessObject() {
        load();
    }

    private void load() {
        String line;
        String cvsSplitBy = ",";

        try (InputStream inputStream = getClass().getResourceAsStream("/data/" + csvFile);
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] data = line.split(cvsSplitBy);

                try {
                    String name = data[0];
                    String pos = data[1];
                    int age = Integer.parseInt(data[2]);
                    String teamName = data[3];
                    int season = Integer.parseInt(data[4]);
                    int gamesPlayed = Integer.parseInt(data[5]);
                    double minutesPlayed = Double.parseDouble(data[6]);
                    double fgPercentage = Double.parseDouble(data[7]);
                    double threePtPercentage = Double.parseDouble(data[8]);
                    double ftPercentage = Double.parseDouble(data[9]);
                    double trb = Double.parseDouble(data[10]);
                    double ast = Double.parseDouble(data[11]);
                    double tov = Double.parseDouble(data[12]);
                    double stl = Double.parseDouble(data[13]);
                    double blk = Double.parseDouble(data[14]);
                    double pf = Double.parseDouble(data[15]);
                    double pts = Double.parseDouble(data[16]);

                    Team team = teamMap.get(teamName);
                    if (team == null) {
                        team = new Team(nextTeamId++, teamName, "N/A", new ArrayList<>(), 0, 0, "N/A", new HashMap<>());
                        teamMap.put(teamName, team);
                    }

                    Player player = playerMap.get(name);
                    if (player == null) {
                        player = new Player(nextPlayerId++, name, team, pos, age, 0, 0, new ArrayList<>());
                        playerMap.put(name, player);
                    }

                    SeasonStats seasonStats = new SeasonStats(season, pts, ast, trb, fgPercentage, gamesPlayed, minutesPlayed, threePtPercentage, player);
                    player.getCareerStats().add(seasonStats);
                    team.getPlayers().add(player);

                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.out.println("Skipping malformed line: " + line);
                }
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Player> getPlayerByName(String playerName) {
        return Optional.ofNullable(playerMap.get(playerName));
    }

    @Override
    public Optional<Team> getTeamByName(String teamName) {
        return Optional.ofNullable(teamMap.get(teamName));
    }

    @Override
    public String getAiInsight(String prompt) {
        // The project's Ollama client dependency exposes a different API
        // between versions. To keep compilation simple and avoid
        // depending on a specific client surface here, return a
        // placeholder string. Integrate a real client or HTTP call
        // when you confirm the correct library API/version.
        return "AI insight (stub): " + prompt;
    }
}
