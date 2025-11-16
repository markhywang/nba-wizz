package data_access;

import com.google.cloud.aiplatform.v1.EndpointName;
import com.google.cloud.aiplatform.v1.PredictResponse;
import com.google.cloud.aiplatform.v1.PredictionServiceClient;
import com.google.cloud.aiplatform.v1.PredictionServiceSettings;
import com.google.protobuf.Value;
import com.google.protobuf.util.JsonFormat;
import entity.Player;
import entity.SeasonStats;
import entity.Team;
import io.github.cdimascio.dotenv.Dotenv;
import use_case.generate_insights.GenerateInsightsDataAccessInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
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
        LOGGER.info("Prompt sent to Gemini API: " + prompt);
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("GEMINI_API_KEY");

        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = System.getenv("GEMINI_API_KEY");
        }

        if (apiKey == null || apiKey.isEmpty()) {
            LOGGER.severe("GEMINI_API_KEY environment variable not set.");
            return "Error: API key not configured. Please set the GEMINI_API_KEY environment variable.";
        }

        String projectId = "gcp-java-424801";
        String location = "us-central1";
        String modelName = "gemini-pro";

        try {
            String endpoint = String.format("%s-aiplatform.googleapis.com:443", location);
            PredictionServiceSettings predictionServiceSettings =
                PredictionServiceSettings.newBuilder().setEndpoint(endpoint).build();

            try (PredictionServiceClient predictionServiceClient =
                PredictionServiceClient.create(predictionServiceSettings)) {
                final EndpointName endpointName =
                    EndpointName.ofProjectLocationPublisherModelName(projectId, location, "google", modelName);

                String escapedPrompt = prompt.replace("\"", "\\\"").replace("\n", "\\n");
                String instanceJson = String.format("{\"contents\":[{\"parts\":[{\"text\":\"%s\"}]}]}", escapedPrompt);

                Value.Builder instanceBuilder = Value.newBuilder();
                JsonFormat.parser().merge(instanceJson, instanceBuilder);
                java.util.List<Value> instances = java.util.Collections.singletonList(instanceBuilder.build());

                Value.Builder parametersBuilder = Value.newBuilder();
                JsonFormat.parser().merge("{}", parametersBuilder);
                Value parameters = parametersBuilder.build();


                PredictResponse predictResponse =
                    predictionServiceClient.predict(endpointName, instances, parameters);

                Value prediction = predictResponse.getPredictions(0);
                String text = prediction.getStructValue()
                    .getFieldsOrThrow("candidates")
                    .getListValue().getValues(0)
                    .getStructValue().getFieldsOrThrow("content")
                    .getStructValue().getFieldsOrThrow("parts")
                    .getListValue().getValues(0)
                    .getStructValue().getFieldsOrThrow("text")
                    .getStringValue();
                return text;
            }
        } catch (IOException e) {
            LOGGER.log(java.util.logging.Level.SEVERE, "Error generating AI insight", e);
            return "Error generating AI insight: " + e.getMessage();
        }
    }
}