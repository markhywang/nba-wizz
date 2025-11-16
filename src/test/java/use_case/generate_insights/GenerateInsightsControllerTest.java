package use_case.generate_insights;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import interface_adapter.generate_insights.GenerateInsightsController;

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
    void testExecute() throws InterruptedException {
        String playerName = "LeBron James";
        generateInsightsController.execute(playerName, "player");

        Thread.sleep(1000); // Wait for the SwingWorker to finish

        verify(generateInsightsInteractor).execute(new GenerateInsightsInputData(playerName, "player"));
    }
}
