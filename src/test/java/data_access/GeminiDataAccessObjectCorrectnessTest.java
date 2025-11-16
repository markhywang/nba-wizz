package data_access;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

public class GeminiDataAccessObjectCorrectnessTest {

    // NOTE: These tests might fail if the Gemini API quota is exceeded.
    // Please check your Google Cloud project's API & Services -> Quotas page.
    private GeminiDataAccessObject dataAccessObject;

    @BeforeEach
    void setUp() {
        dataAccessObject = new GeminiDataAccessObject();
    }

    // @Test
    // void testAskQuestion_KnownFact() throws IOException {
    //     String question = "How many points per game did LeBron James average in the 2016 season?";
    //     String context = dataAccessObject.getDatasetContent();
    //     String answer = dataAccessObject.getAnswer(question, context);

    //     assertNotNull(answer, "The answer should not be null.");
    //     assertFalse(answer.toLowerCase().contains("cannot answer"), "The AI should be able to answer the question as the 2016 data is in the dataset.");
    //     assertTrue(answer.toLowerCase().contains("lebron james") && answer.toLowerCase().contains("2016") && answer.toLowerCase().contains("25.3"), "The AI should provide the correct points per game for LeBron James in 2016.");
    // }

    @Test
    void testPlayerComparison_KnownPlayers() {
        String player1Name = "LeBron James";
        String player2Name = "Stephen Curry";

        dataAccessObject.getPlayerByName(player1Name).ifPresent(player1 -> {
            dataAccessObject.getPlayerByName(player2Name).ifPresent(player2 -> {
                String comparison = dataAccessObject.getPlayerComparison(player1, player2);

                assertNotNull(comparison, "The comparison should not be null.");
                assertFalse(comparison.toLowerCase().contains("error"), "The comparison should not contain an error.");
                assertTrue(comparison.toLowerCase().contains("lebron") && comparison.toLowerCase().contains("curry"), "The comparison should mention both players.");
            });
        });
    }

    // @Test
    // void testAskQuestion_SpecificPlayerStat() throws IOException {
    //     String question = "What was Kevin Durant's field goal percentage in the 2014 season?";
    //     String context = dataAccessObject.getDatasetContent();
    //     String answer = dataAccessObject.getAnswer(question, context);

    //     assertNotNull(answer, "The answer should not be null.");
    //     assertFalse(answer.toLowerCase().contains("cannot answer"), "The AI should be able to answer the question as the 2014 data is in the dataset.");
    //     assertTrue(answer.toLowerCase().contains("kevin durant") && answer.toLowerCase().contains("2014") && answer.toLowerCase().contains("50.3"), "The AI should provide the correct field goal percentage for Kevin Durant in 2014.");
    // }

    @Test
    void testPlayerComparison_SpecificCriteria() throws IOException {
        String question = "Who averaged more assists in the 2021 season, Chris Paul or Russell Westbrook?";
        String context = dataAccessObject.getDatasetContent();
        String answer = dataAccessObject.getAnswer(question, context);

        assertNotNull(answer, "The answer should not be null.");
        assertFalse(answer.toLowerCase().contains("cannot answer"), "The AI should be able to answer the question as the 2021 data is in the dataset.");
        assertTrue(answer.toLowerCase().contains("chris paul") && answer.toLowerCase().contains("russell westbrook") && answer.toLowerCase().contains("2021"), "The AI should provide a comparison of assists for Chris Paul and Russell Westbrook in 2021.");
    }

    @Test
    void testAskQuestion_TeamPerformance() throws IOException {
        String question = "How many wins did the Golden State Warriors have in the 2016 season?";
        String context = dataAccessObject.getDatasetContent();
        String answer = dataAccessObject.getAnswer(question, context);

        assertNotNull(answer, "The answer should not be null.");
        assertTrue(answer.toLowerCase().contains("cannot answer") && answer.toLowerCase().contains("dataset"), "The AI should not be able to answer the question.");
    }

    @Test
    void testAskQuestion_NonExistentPlayer() throws IOException {
        String question = "What are the stats for the player John Doe?";
        String context = dataAccessObject.getDatasetContent();
        String answer = dataAccessObject.getAnswer(question, context);

        assertNotNull(answer, "The answer should not be null.");
        assertTrue(answer.toLowerCase().contains("cannot answer") && answer.toLowerCase().contains("dataset"), "The AI should indicate that the player does not exist.");
    }

    @Test
    void testAskQuestion_IncorrectButPlausibleAnswer() throws IOException {
        // This tests if the AI hallucinates a wrong but plausible answer if the context is misleading or insufficient.
        // For example, asking for a stat that is not directly in the dataset but could be inferred incorrectly.
        String question = "What was Stephen Curry's highest 3-point percentage in a single season?";
        String context = dataAccessObject.getDatasetContent();
        String answer = dataAccessObject.getAnswer(question, context);

        assertNotNull(answer, "The answer should not be null.");
        // Assuming the dataset does not explicitly state 'highest 3-point percentage' but has season-by-season stats.
        // The AI should either state it cannot answer directly or provide a range/list of seasons.
        // It should NOT just pick a random high percentage and state it as the highest without evidence.
        // assertFalse(answer.toLowerCase().contains("i cannot answer"), "The AI should be able to provide some information or a cautious answer.");
        // This assertion needs to be carefully crafted based on expected AI behavior with the given dataset.
        // For now, we'll check if it doesn't give a definitive, potentially hallucinated, single highest value.
        // A more robust test would involve checking if it lists multiple seasons or explicitly states it's an estimate.
        assertFalse(answer.matches(".*\\d{1,2}\\.\\d% in \\d{4}.*"), "The AI should not give a single definitive highest percentage if not explicitly in data.");
    }

    @Test
    void testAskQuestion_OutOfContextQuestion() throws IOException {
        String question = "What is the capital of France?";
        String context = dataAccessObject.getDatasetContent();
        String answer = dataAccessObject.getAnswer(question, context);

        assertNotNull(answer, "The answer should not be null.");
        assertTrue(answer.toLowerCase().contains("cannot answer") && answer.toLowerCase().contains("dataset"), "The AI should indicate it cannot answer out-of-context questions.");
    }

    @Test
    void testAskQuestion_AmbiguousQuestion() throws IOException {
        String question = "Who is the best player?";
        String context = dataAccessObject.getDatasetContent();
        String answer = dataAccessObject.getAnswer(question, context);

        assertNotNull(answer, "The answer should not be null.");
        // For ambiguous questions, the AI should ideally ask for clarification or state that 'best' is subjective.
        // It should not definitively name one player without context.
        assertTrue(answer.length() > 25, "The AI should provide a detailed explanation for ambiguous questions, not just a short answer.");
    }
}