package data_access;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class GeminiDataAccessObjectTest {

    private GeminiDataAccessObject dataAccessObject;

    @BeforeEach
    void setUp() {
        dataAccessObject = new GeminiDataAccessObject();
    }

    @Test
    void testGetAiInsight() {
        // For example, by running `gcloud auth application-default login`.
        String playerName = "LeBron James";
        String prompt = "Give me a summary of " + playerName + "'s career.";

        String insight = dataAccessObject.getAiInsight(prompt);
        System.out.println("Gemini API Output for LeBron James:\n" + insight);

        assertNotNull(insight, "The returned insight should not be null.");
        assertFalse(insight.isEmpty(), "The returned insight should not be empty.");
        assertFalse(insight.startsWith("Error:"), "The returned insight should not be an error message.");
    }
}
