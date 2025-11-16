package use_case.generate_insights;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

class GenerateInsightsControllerTest {

    @Mock
    private GenerateInsightsInputBoundary generateInsightsInteractor;

    @InjectMocks
    private GenerateInsightsController generateInsightsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecute() {
        String playerName = "LeBron James";
        generateInsightsController.execute(playerName);

        verify(generateInsightsInteractor).execute(new GenerateInsightsInputData(playerName));
    }
}
