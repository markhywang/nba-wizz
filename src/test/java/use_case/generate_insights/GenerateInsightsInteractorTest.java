package use_case.generate_insights;

import data_access.GenerateInsightsDataAccessInterface;
import entity.AIInsight;
import entity.Player;
import entity.SeasonStats;
import entity.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
    void testExecute_PlayerSuccess_NoStats() {
        // Arrange
        Player mockPlayer = new Player(1, "LeBron James", null, "SF", 38, 0, 0, new ArrayList<>());
        when(generateInsightsDataAccessInterface.getPlayerByName("LeBron James")).thenReturn(Optional.of(mockPlayer));
        String insight = "This is an insight.";
        when(generateInsightsDataAccessInterface.getAiInsight(any(String.class))).thenReturn(insight);

        GenerateInsightsInputData inputData = new GenerateInsightsInputData("LeBron James", "Player");

        // Act
        generateInsightsInteractor.execute(inputData);

        // Assert
        verify(generateInsightsPresenter).prepareLoadingView();
        verify(generateInsightsDataAccessInterface).getAiInsight(any(String.class));
        verify(generateInsightsPresenter).prepareSuccessView(any(GenerateInsightsOutputData.class));
        verify(generateInsightsPresenter, never()).prepareFailView(any(String.class));
    }

    @Test
    void testExecute_PlayerSuccess_WithStats() {
        // Arrange
        List<SeasonStats> statsList = new ArrayList<>();
        SeasonStats stats = new SeasonStats(2023, 25.0, 7.0, 8.0, 50.0, 60, 35.0, 30.0, null);
        statsList.add(stats);
        
        Player mockPlayer = new Player(1, "LeBron James", null, "SF", 38, 0, 0, statsList);
        when(generateInsightsDataAccessInterface.getPlayerByName("LeBron James")).thenReturn(Optional.of(mockPlayer));
        
        when(generateInsightsDataAccessInterface.getAiInsight(any(String.class))).thenReturn("Insight with stats");

        GenerateInsightsInputData inputData = new GenerateInsightsInputData("LeBron James", "Player");

        // Act
        generateInsightsInteractor.execute(inputData);

        // Assert
        verify(generateInsightsPresenter).prepareSuccessView(any(GenerateInsightsOutputData.class));
    }

    @Test
    void testExecute_PlayerFailure_APIError() {
        // Arrange
        Player mockPlayer = new Player(1, "LeBron James", null, "SF", 38, 0, 0, new ArrayList<>());
        when(generateInsightsDataAccessInterface.getPlayerByName("LeBron James")).thenReturn(Optional.of(mockPlayer));
        when(generateInsightsDataAccessInterface.getAiInsight(any(String.class))).thenThrow(new RuntimeException("API error"));

        GenerateInsightsInputData inputData = new GenerateInsightsInputData("LeBron James", "Player");

        // Act
        generateInsightsInteractor.execute(inputData);

        // Assert
        verify(generateInsightsPresenter).prepareFailView("API error");
        verify(generateInsightsPresenter, never()).prepareSuccessView(any(GenerateInsightsOutputData.class));
    }

    @Test
    void testExecute_PlayerNotFound() {
        // Arrange
        when(generateInsightsDataAccessInterface.getPlayerByName("Unknown Player")).thenReturn(Optional.empty());
        GenerateInsightsInputData inputData = new GenerateInsightsInputData("Unknown Player", "Player");

        // Act
        generateInsightsInteractor.execute(inputData);

        // Assert
        verify(generateInsightsPresenter).prepareFailView("Player not found.");
    }

    @Test
    void testExecute_TeamSuccess_WithPlayers() {
        // Arrange
        Player p1 = new Player(1, "Player One", null, "PG", 25, 190, 90, new ArrayList<>());
        // Add stats to player to cover inner loop in createTeamPrompt
        List<SeasonStats> statsList = new ArrayList<>();
        statsList.add(new SeasonStats(2023, 10.0, 2.0, 3.0, 45.0, 80, 30.0, 35.0, null));
        Player p2 = new Player(2, "Player Two", null, "SG", 28, 195, 95, statsList);
        
        List<Player> roster = Arrays.asList(p1, p2, p1); // Duplicate p1 to test duplication check

        Team mockTeam = new Team(10, "Lakers", "Los Angeles", roster, 40, 42, "West", Collections.emptyMap());
        
        when(generateInsightsDataAccessInterface.getTeamByName("Lakers")).thenReturn(Optional.of(mockTeam));
        when(generateInsightsDataAccessInterface.getAiInsight(any(String.class))).thenReturn("Team Insight");

        GenerateInsightsInputData inputData = new GenerateInsightsInputData("Lakers", "Team");

        // Act
        generateInsightsInteractor.execute(inputData);

        // Assert
        verify(generateInsightsDataAccessInterface).getAiInsight(any(String.class));
        verify(generateInsightsPresenter).prepareSuccessView(any(GenerateInsightsOutputData.class));
    }

    @Test
    void testExecute_TeamNotFound() {
        // Arrange
        when(generateInsightsDataAccessInterface.getTeamByName("Unknown Team")).thenReturn(Optional.empty());
        GenerateInsightsInputData inputData = new GenerateInsightsInputData("Unknown Team", "Team");

        // Act
        generateInsightsInteractor.execute(inputData);

        // Assert
        verify(generateInsightsPresenter).prepareFailView("Team not found.");
    }

    @Test
    void testExecute_InvalidEntityType() {
        // Arrange
        GenerateInsightsInputData inputData = new GenerateInsightsInputData("Name", "Coach");

        // Act
        generateInsightsInteractor.execute(inputData);

        // Assert
        verify(generateInsightsPresenter).prepareFailView("Invalid entity type.");
    }

    @Test
    void testExecute_EmptyName() {
        // Arrange
        GenerateInsightsInputData inputData = new GenerateInsightsInputData("", "Player");

        // Act
        generateInsightsInteractor.execute(inputData);

        // Assert
        verify(generateInsightsPresenter).prepareFailView("Player or team name cannot be empty.");
    }
    
    @Test
    void testExecute_NullName() {
        // Arrange
        GenerateInsightsInputData inputData = new GenerateInsightsInputData(null, "Player");

        // Act
        generateInsightsInteractor.execute(inputData);

        // Assert
        verify(generateInsightsPresenter).prepareFailView("Player or team name cannot be empty.");
    }
}
