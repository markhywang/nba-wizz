package data_access;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GeminiDataAccessObjectTest {

    private GeminiDataAccessObject dataAccessObject;

    @BeforeEach
    void setUp() {
        dataAccessObject = new GeminiDataAccessObject();
    }

    @Test
    void testGetAiInsight() {
        // IMPORTANT: This test requires a valid GEMINI_API_KEY environment variable to be set.
        String playerName = "LeBron James";
        String prompt = "Give me a summary of " + playerName + "'s career.";

        String insight = dataAccessObject.getAiInsight(prompt);

        assertNotNull(insight, "The returned insight should not be null.");
        assertFalse(insight.isEmpty(), "The returned insight should not be empty.");
        assertFalse(insight.startsWith("Error:"), "The returned insight should not be an error message.");
    }
}
