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
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

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
        CompletableFuture<String> future = new CompletableFuture<>();
        StringBuilder responseBuilder = new StringBuilder();
        callGeminiApi(prompt, 
            responseBuilder::append, // Accumulate each chunk
            () -> future.complete(responseBuilder.toString()), // Complete with full response when done
            future::completeExceptionally);
        return future.join();
    }

    @Override
    public void getAnswer(String question, String context, Consumer<String> onData, Runnable onComplete, Consumer<Exception> onError) {
        String prompt = createQuestionPrompt(question, context);
        LOGGER.info("Prompt sent to Gemini: " + prompt);
        callGeminiApi(prompt, onData, onComplete, onError);
    }

    @Override
    public String getAnswerSync(String question, String context) throws IOException {
        String prompt = createQuestionPrompt(question, context);
        LOGGER.info("Prompt sent to Gemini: " + prompt);
        CompletableFuture<String> future = new CompletableFuture<>();
        StringBuilder responseBuilder = new StringBuilder();
        callGeminiApi(prompt, 
            responseBuilder::append, // Accumulate each chunk
            () -> future.complete(responseBuilder.toString()), // Complete with full response when done
            (Exception e) -> future.completeExceptionally(new IOException("Error getting answer: " + e.getMessage())));
        try {
            return future.join();
        } catch (Exception e) {
            throw new IOException("Error getting answer: " + e.getMessage(), e);
        }
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
        CompletableFuture<String> future = new CompletableFuture<>();
        StringBuilder responseBuilder = new StringBuilder();
        callGeminiApi(prompt, 
            responseBuilder::append, // Accumulate each chunk
            () -> future.complete(responseBuilder.toString()), // Complete with full response when done
            future::completeExceptionally);
        return future.join();
    }

    private String createQuestionPrompt(String question, String context) {
        return "You are a knowledgeable basketball expert and data analyst. Answer the following question using the provided statistical data as your primary source of truth.\n\n" +
                "Dataset (1982-2024):\n" +
                "```csv\n" +
                context +
                "```\n\n" +
                "Guidelines:\n" +
                "1. Prioritize the provided statistics. If the data contradicts common knowledge, point out what the data says.\n" +
                "2. You MAY use your own external basketball knowledge to provide context, explain trends, or mention awards/achievements not in the CSV, but strictly base specific stat claims on the provided data.\n" +
                "3. Answer in a natural, conversational, and engaging tone. Use complete sentences and paragraphs. Do not just list numbers.\n" +
                "4. If the provided data is insufficient to answer the core of the question and you cannot answer from general knowledge without hallucinating stats, state that the specific data is missing.\n\n" +
                "Question: " + question + "\n\n" +
                "Answer:";
    }

    private String createPlayerComparisonPrompt(Player player1, Player player2) {
        return "You are a basketball expert. Provide a detailed and engaging comparison between " + player1.getName() + " and " + player2.getName() + ".\n\n" +
                "Stats for " + player1.getName() + ":\n" + getPlayerStatsAsString(player1) + "\n" +
                "Stats for " + player2.getName() + ":\n" + getPlayerStatsAsString(player2) + "\n\n" +
                "Instructions:\n" +
                "1. Use the provided stats as a foundation for your analysis.\n" +
                "2. Incorporate your own knowledge about their playstyles, careers, impact, and legacy to provide a complete picture.\n" +
                "3. Write in a conversational, natural manner using paragraphs. Do not simply list the stats provided.\n" +
                "4. Conclude with a summary of who might be considered 'better' or how they differ in value.\n\n" +
                "Comparison:";
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

    private void callGeminiApi(String prompt, Consumer<String> onData, Runnable onComplete, Consumer<Exception> onError) {
        new Thread(() -> {
            try {
                Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
                String apiKey = dotenv.get("GEMINI_API_KEY");

                if (apiKey == null || apiKey.isEmpty()) {
                    apiKey = System.getenv("GEMINI_API_KEY");
                    if (apiKey == null || apiKey.isEmpty()) {
                        apiKey = System.getenv("GOOGLE_API_KEY");
                    }
                }

                if (apiKey == null || apiKey.isEmpty()) {
                    LOGGER.severe("API key not set. Please set GOOGLE_API_KEY or GEMINI_API_KEY environment variable or in .env file.");
                    onError.accept(new Exception("Error: API key not set."));
                    return;
                }

                Client client = Client.builder().apiKey(apiKey).build();
                try (ResponseStream<GenerateContentResponse> responseStream =
                             client.models.generateContentStream("gemini-2.5-flash", prompt, null)) {

                    for (GenerateContentResponse response : responseStream) {
                        String text = response.text();
                        if (text != null) {
                            onData.accept(text);
                        }
                    }
                }
                onComplete.run();
            } catch (NoSuchElementException e) {
                LOGGER.severe("Gemini API returned no content: " + e.getMessage());
                onError.accept(new Exception("Error: AI returned no content."));
            } catch (Exception e) {
                LOGGER.severe("Gemini API error: " + e.getMessage());
                onError.accept(new Exception("Error generating AI insight: " + e.getMessage()));
            }
        }).start();
    }
}
