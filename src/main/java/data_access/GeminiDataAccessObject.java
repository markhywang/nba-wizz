package data_access;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import entity.Player;
import entity.SeasonStats;
import entity.Team;
import use_case.generate_insights.GenerateInsightsDataAccessInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public class GeminiDataAccessObject implements GenerateInsightsDataAccessInterface {

    private static final java.util.logging.Logger LOGGER =
            java.util.logging.Logger.getLogger(GeminiDataAccessObject.class.getName());

    private final String csvFile = "PlayerStatsDataset.csv";
    private final Map<String, Player> playerMap = new HashMap<>();
    private final Map<String, Team> teamMap = new HashMap<>();
    private int nextPlayerId = 1;
    private int nextTeamId = 1;

    public GeminiDataAccessObject() {
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
                    double pts = Double.parseDouble(data[16]);

                    Team team = teamMap.computeIfAbsent(teamName,
                            k -> new Team(nextTeamId++, teamName, "N/A",
                                    new ArrayList<>(), 0, 0, "N/A", new HashMap<>()));

                    Player player = playerMap.computeIfAbsent(name,
                            k -> new Player(nextPlayerId++, name, team, pos, age, 0, 0, new ArrayList<>()));

                    SeasonStats seasonStats = new SeasonStats(
                            season, pts, ast, trb, fgPercentage, gamesPlayed, minutesPlayed, threePtPercentage, player
                    );

                    player.getCareerStats().add(seasonStats);
                    team.getPlayers().add(player);

                } catch (Exception e) {
                    LOGGER.warning("Skipping malformed line: " + line);
                }
            }

        } catch (IOException e) {
            LOGGER.severe("Failed loading CSV: " + csvFile);
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
        LOGGER.info("Prompt sent to Gemini: " + prompt);

        String apiKey = System.getenv("GEMINI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            // The README mentions GOOGLE_API_KEY, so let's check for that too.
            apiKey = System.getenv("GOOGLE_API_KEY");
        }

        if (apiKey == null || apiKey.isEmpty()) {
            LOGGER.severe("API key not set. Please set GOOGLE_API_KEY or GEMINI_API_KEY environment variable.");
            return "Error: API key not set.";
        }


        try {
            Client client = Client.builder().apiKey(apiKey).build();
            GenerateContentResponse response =
                    client.models.generateContent("gemini-2.5-flash", prompt, null);
            return response.text();
        } catch (NoSuchElementException e) {
            LOGGER.severe("Gemini API returned no content: " + e.getMessage());
            return "Error: AI returned no content.";
        }
        catch (Exception e) {
            LOGGER.severe("Gemini API error: " + e.getMessage());
            return "Error generating AI insight: " + e.getMessage();
        }
    }
}
