package data_access;

import com.google.genai.Client;
import com.google.genai.ResponseStream;
import com.google.genai.types.GenerateContentResponse;
import entity.Player;
import entity.SeasonStats;
import entity.Team;
import io.github.cdimascio.dotenv.Dotenv;
import use_case.ask_question.AskQuestionDataAccessInterface;
import use_case.compare_players.ComparePlayersDataAccessInterface;
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

public class GeminiDataAccessObject implements GenerateInsightsDataAccessInterface, AskQuestionDataAccessInterface, ComparePlayersDataAccessInterface {

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
                    String name = data[0].toLowerCase();
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

                    Player player = playerMap.get(name);
                    if (player == null) {
                        player = new Player(nextPlayerId++, name, team, pos, age, 0, 0, new ArrayList<>());
                        playerMap.put(name, player);
                    }


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
        return Optional.ofNullable(playerMap.get(playerName.toLowerCase()));
    }

    @Override
    public Optional<Team> getTeamByName(String teamName) {
        return Optional.ofNullable(teamMap.get(teamName));
    }

    @Override
    public String getAiInsight(String prompt) {
        LOGGER.info("Prompt sent to Gemini: " + prompt);
        return callGeminiApi(prompt);
    }

    @Override
    public String getAnswer(String question, String context) {
        String prompt = createQuestionPrompt(question, context);
        LOGGER.info("Prompt sent to Gemini: " + prompt);
        return callGeminiApi(prompt);
    }

    @Override
    public String getDatasetContent() throws IOException {
        StringBuilder content = new StringBuilder();
        try (InputStream inputStream = getClass().getResourceAsStream("/data/" + csvFile);
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    @Override
    public String getPlayerComparison(Player player1, Player player2) {
        String prompt = createPlayerComparisonPrompt(player1, player2);
        LOGGER.info("Prompt sent to Gemini: " + prompt);
        return callGeminiApi(prompt);
    }

    private String createQuestionPrompt(String question, String context) {
        return "You are a basketball analyst. Your task is to answer questions based on the provided dataset and your general knowledge. " +
                "First, determine if the question is appropriate and related to basketball. If it is inappropriate, off-topic, or harmful, you must refuse to answer by responding with 'I cannot answer this question.'. " +
                "If the question is valid, use the following CSV data to answer it. Do not mention the dataset in your response.\n\n" +
                "Dataset:\n" + context + "\n\n" +
                "Question: " + question + "\n\n" +
                "Answer:";
    }

    private String createPlayerComparisonPrompt(Player player1, Player player2) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a basketball analyst. Provide a detailed comparison of the following two players, considering their stats and your own knowledge. ");
        prompt.append("Discuss their strengths, weaknesses, and potential impact on a team.\n\n");
        prompt.append("Player 1: ").append(player1.getName()).append("\n");
        prompt.append(getPlayerStatsAsString(player1));
        prompt.append("\nPlayer 2: ").append(player2.getName()).append("\n");
        prompt.append(getPlayerStatsAsString(player2));
        prompt.append("\nComparison:");
        return prompt.toString();
    }

    private String getPlayerStatsAsString(Player player) {
        StringBuilder stats = new StringBuilder();
        stats.append("Position: ").append(player.getPosition()).append("\n");
        if (player.getCareerStats() != null && !player.getCareerStats().isEmpty()) {
            SeasonStats lastSeason = player.getCareerStats().get(player.getCareerStats().size() - 1);
            stats.append("Season: ").append(lastSeason.getSeasonYear()).append("\n");
            stats.append(String.format("Points per game: %.2f\n", lastSeason.getPointsPerGame()));
            stats.append(String.format("Assists per game: %.2f\n", lastSeason.getAssistsPerGame()));
            stats.append(String.format("Rebounds per game: %.2f\n", lastSeason.getReboundsPerGame()));
        }
        return stats.toString();
    }

    private String callGeminiApi(String prompt) {
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("GEMINI_API_KEY");

        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = System.getenv("GEMINI_API_KEY");
            if (apiKey == null || apiKey.isEmpty()) {
                apiKey = System.getenv("GOOGLE_API_KEY");
            }
        }

        if (apiKey == null || apiKey.isEmpty()) {
            LOGGER.severe("API key not set. Please set GOOGLE_API_KEY or GEMINI_API_KEY environment variable or in .env file.");
            return "Error: API key not set.";
        }

        try {
            Client client = Client.builder().apiKey(apiKey).build();
            ResponseStream<GenerateContentResponse> responseStream =
                    client.models.generateContentStream("gemini-2.5-flash", prompt, null);

            StringBuilder insight = new StringBuilder();
            for (GenerateContentResponse response : responseStream) {
                insight.append(response.text());
            }
            responseStream.close();
            return insight.toString();
        } catch (NoSuchElementException e) {
            LOGGER.severe("Gemini API returned no content: " + e.getMessage());
            return "Error: AI returned no content.";
        } catch (Exception e) {
            LOGGER.severe("Gemini API error: " + e.getMessage());
            return "Error generating AI insight: " + e.getMessage();
        }
    }
}
