package use_case.generate_insights;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import interface_adapter.generate_insights.GenerateInsightsController;

import org.mockito.ArgumentCaptor;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
        String entityType = "player";
        generateInsightsController.execute(playerName, entityType);

        Thread.sleep(1000); // Wait for the SwingWorker to finish

        ArgumentCaptor<GenerateInsightsInputData> argumentCaptor = ArgumentCaptor.forClass(GenerateInsightsInputData.class);
        verify(generateInsightsInteractor).execute(argumentCaptor.capture());
        
        GenerateInsightsInputData capturedInputData = argumentCaptor.getValue();
        assertEquals(playerName, capturedInputData.entityName());
        assertEquals(entityType, capturedInputData.entityType());
    }
}
