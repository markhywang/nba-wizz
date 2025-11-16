package use_case.generate_insights;

import data_access.GenerateInsightsDataAccessInterface;
import entity.AIInsight;
import entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GenerateInsightsInteractorTest {

    @Mock
    private GenerateInsightsDataAccessInterface generateInsightsDataAccessInterface;

    @Mock
    private GenerateInsightsOutputBoundary generateInsightsPresenter;

    @InjectMocks
    private GenerateInsightsInteractor generateInsightsInteractor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecute_Success() {
        // Arrange
        Player mockPlayer = new Player(1, "LeBron James", null, "SF", 38, 0, 0, new ArrayList<>());
        when(generateInsightsDataAccessInterface.getPlayerByName("LeBron James")).thenReturn(Optional.of(mockPlayer));
        String insight = "This is an insight.";
        when(generateInsightsDataAccessInterface.getAiInsight(any(String.class))).thenReturn(insight);

        GenerateInsightsInputData inputData = new GenerateInsightsInputData("LeBron James", "Player");

        // Act
        generateInsightsInteractor.execute(inputData);

        // Assert
        verify(generateInsightsDataAccessInterface).getAiInsight(any(String.class));
        verify(generateInsightsPresenter).prepareSuccessView(any(GenerateInsightsOutputData.class));
        verify(generateInsightsPresenter, never()).prepareFailView(any(String.class));
    }

    @Test
    void testExecute_Failure() {
        // Arrange
        Player mockPlayer = new Player(1, "LeBron James", null, "SF", 38, 0, 0, new ArrayList<>());
        when(generateInsightsDataAccessInterface.getPlayerByName("LeBron James")).thenReturn(Optional.of(mockPlayer));
        when(generateInsightsDataAccessInterface.getAiInsight(any(String.class))).thenThrow(new RuntimeException("API error"));

        GenerateInsightsInputData inputData = new GenerateInsightsInputData("LeBron James", "Player");

        // Act
        generateInsightsInteractor.execute(inputData);

        // Assert
        verify(generateInsightsDataAccessInterface).getAiInsight(any(String.class));
        verify(generateInsightsPresenter, never()).prepareSuccessView(any(GenerateInsightsOutputData.class));
        verify(generateInsightsPresenter).prepareFailView("API error");
    }
}
