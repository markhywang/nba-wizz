package data_access;

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
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GeminiDataAccessObject implements GenerateInsightsDataAccessInterface {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(GeminiDataAccessObject.class.getName());
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
                    LOGGER.warning("Skipping malformed line: " + line);
                }
            }
        } catch (IOException | NullPointerException e) {
            LOGGER.log(java.util.logging.Level.SEVERE, "Failed loading CSV: " + csvFile, e);
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
        // Similar configuration as before, but with timeouts and logging.
        String apiUrl = System.getenv("GEMINI_API_URL");
        if (apiUrl == null || apiUrl.isEmpty()) {
            // Default to Google's Generative Language REST endpoint for text-bison-001.
            apiUrl = "https://generativelanguage.googleapis.com/v1/models/text-bison-001:generate";
        }

        String apiKey = System.getenv("GEMINI_API_KEY");
        String bearer = System.getenv("GEMINI_AUTH_BEARER");

        try {
            java.net.http.HttpClient client = java.net.http.HttpClient.newBuilder()
                    .connectTimeout(java.time.Duration.ofSeconds(10))
                    .build();

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.node.ObjectNode body = mapper.createObjectNode();
            com.fasterxml.jackson.databind.node.ObjectNode promptNode = mapper.createObjectNode();
            promptNode.put("text", prompt);
            body.set("prompt", promptNode);
            // Google uses camelCase for this field name
            body.put("maxOutputTokens", 512);

            String jsonBody = mapper.writeValueAsString(body);

            String requestUrl = apiUrl;
            if (apiKey != null && !apiKey.isEmpty() && !requestUrl.contains("?")) {
                requestUrl = requestUrl + "?key=" + java.net.URLEncoder.encode(apiKey, java.nio.charset.StandardCharsets.UTF_8);
            }

            java.net.http.HttpRequest.Builder reqBuilder = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(requestUrl))
                    .header("Content-Type", "application/json")
                    .timeout(java.time.Duration.ofSeconds(60))
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(jsonBody));

            if (bearer != null && !bearer.isEmpty()) {
                reqBuilder.header("Authorization", "Bearer " + bearer);
            }

            java.net.http.HttpRequest request = reqBuilder.build();

            java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                LOGGER.warning("LLM endpoint returned status " + response.statusCode() + ": " + response.body());
                return "Error: LLM endpoint returned status " + response.statusCode();
            }

            com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(response.body());

            String respText = null;
            if (root.has("candidates") && root.get("candidates").isArray() && root.get("candidates").size() > 0) {
                respText = root.get("candidates").get(0).path("content").asText(null);
            }
            if ((respText == null || respText.isEmpty()) && root.has("output")) {
                respText = root.path("output").asText(null);
            }
            if ((respText == null || respText.isEmpty()) && root.has("response")) {
                respText = root.path("response").asText(null);
            }
            if ((respText == null || respText.isEmpty()) && root.has("text")) {
                respText = root.path("text").asText(null);
            }

            if (respText == null || respText.isEmpty()) {
                java.util.Iterator<java.util.Map.Entry<String, com.fasterxml.jackson.databind.JsonNode>> it = root.fields();
                while (it.hasNext() && (respText == null || respText.isEmpty())) {
                    com.fasterxml.jackson.databind.JsonNode node = it.next().getValue();
                    if (node.isTextual()) respText = node.asText();
                }
            }

            if (respText == null || respText.isEmpty()) {
                return response.body();
            }

            return respText;
        } catch (Exception e) {
            LOGGER.log(java.util.logging.Level.SEVERE, "Error generating AI insight", e);
            return "Error generating AI insight: " + e.getMessage();
        }
    }
}
