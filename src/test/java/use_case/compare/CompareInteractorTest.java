package use_case.compare;

import data_access.PlayerDataAccessInterface;
import data_access.TeamDataAccessInterface;
import entity.Normalization;
import entity.Player;
import entity.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CompareInteractorTest {

    PlayerDataAccessInterface playerDAO;
    TeamDataAccessInterface teamDAO;
    CompareOutputBoundary presenter;
    CompareInteractor interactor;

    @BeforeEach
    void setup() {
        playerDAO = mock(PlayerDataAccessInterface.class);
        teamDAO = mock(TeamDataAccessInterface.class);
        presenter = mock(CompareOutputBoundary.class);

        interactor = new CompareInteractor(playerDAO, teamDAO, presenter);
    }

    @Test
    void testLessThanTwoInputs() {
        CompareInputData in = new CompareInputData(
                CompareInputData.EntityType.PLAYER,
                List.of("LeBron"), 2020, 2021, "Basic", null);

        interactor.execute(in);
        verify(presenter).presentError("Select at least two players or teams.");
    }

    @Test
    void testMoreThanFiveInputs() {
        CompareInputData in = new CompareInputData(
                CompareInputData.EntityType.PLAYER,
                List.of("A","B","C","D","E","F"), 2019, 2020, "Basic", null);

        interactor.execute(in);
        verify(presenter).presentError("You can compare at most five.");
    }

    @Test
    void testInvalidSeasonRange() {
        CompareInputData in = new CompareInputData(
                CompareInputData.EntityType.PLAYER,
                List.of("A","B"), 2022, 2020, "Basic", null);

        interactor.execute(in);
        verify(presenter).presentError("Season start must be <= season end.");
    }

    @Test
    void testPlayerNotFound() {
        when(playerDAO.getPlayerByName("CURRY")).thenReturn(null);

        CompareInputData in = new CompareInputData(
                CompareInputData.EntityType.PLAYER,
                List.of("CURRY","KD"), 2018,2019,"Basic",null);

        interactor.execute(in);
        verify(presenter).presentError("Player CURRY not found.");
    }

    @Test
    void testTeamNotFound() {
        when(teamDAO.getTeamByName("Lakers")).thenReturn(null);

        CompareInputData in = new CompareInputData(
                CompareInputData.EntityType.TEAM,
                List.of("Lakers","Celtics"), 2018,2019,"Basic",null);

        interactor.execute(in);
        verify(presenter).presentError("Team Lakers not found.");
    }

    @Test
    void testUnsupportedMetricsSource() {
        when(playerDAO.getPlayerByName(any())).thenReturn(mock(Player.class));
        when(playerDAO.getAggregatedMetrics(anyString(),anyInt(),anyInt(),any(),any()))
                .thenThrow(new UnsupportedOperationException());

        CompareInputData in = new CompareInputData(
                CompareInputData.EntityType.PLAYER,
                List.of("A","B"), 2019,2020,"Basic",null);

        interactor.execute(in);
        verify(presenter).presentError("Comparison not supported by data source.");
    }

    @Test
    void testSuccessfulPlayerCompare() {

        Map<String,Double> p1 = Map.of("PTS",30.0,"TRB",8.0,"AST",6.0,"STL",2.0,"BLK",1.0);
        Map<String,Double> p2 = Map.of("PTS",28.0,"TRB",10.0,"AST",9.0,"STL",1.0,"BLK",2.0);

        when(playerDAO.getPlayerByName("A")).thenReturn(mock(Player.class));
        when(playerDAO.getPlayerByName("B")).thenReturn(mock(Player.class));
        when(playerDAO.getAggregatedMetrics(eq("A"),anyInt(),anyInt(),any(),any())).thenReturn(p1);
        when(playerDAO.getAggregatedMetrics(eq("B"),anyInt(),anyInt(),any(),any())).thenReturn(p2);

        CompareInputData in = new CompareInputData(
                CompareInputData.EntityType.PLAYER,
                List.of("A","B"), 2020,2020,"Basic",null);

        interactor.execute(in);
        verify(presenter).present(any());
    }

    @Test
    void testMissingDataNoticeCase() {

        when(playerDAO.getPlayerByName(any())).thenReturn(mock(Player.class));
        when(playerDAO.getAggregatedMetrics(eq("A"),anyInt(),anyInt(),any(),any()))
                .thenThrow(new RuntimeException());

        CompareInputData in = new CompareInputData(
                CompareInputData.EntityType.PLAYER,
                List.of("A","B"),2020,2021,"Basic",null);

        interactor.execute(in);
        verify(presenter).present(any());
    }

    @Test
    void testSwitchToMainMenu() {
        interactor.switchToMainMenu();
        verify(presenter).switchToMainMenu();
    }

    @Test
    void testNullValuesInMetricComparison() {

        when(playerDAO.getPlayerByName(anyString())).thenReturn(mock(Player.class));

        Map<String,Double> p1 = new HashMap<>();
        p1.put("PTS", null);

        Map<String,Double> p2 = new HashMap<>();
        p2.put("PTS", null);

        when(playerDAO.getAggregatedMetrics(eq("A"),anyInt(),anyInt(),any(),any())).thenReturn(p1);
        when(playerDAO.getAggregatedMetrics(eq("B"),anyInt(),anyInt(),any(),any())).thenReturn(p2);

        CompareInputData in = new CompareInputData(
                CompareInputData.EntityType.PLAYER,
                List.of("A","B"),
                2020,2020,"Basic",null);

        interactor.execute(in);

        verify(presenter).present(any());
    }

    @Test
    void testMetricNullValuesStillProducesRows() {
        when(playerDAO.getPlayerByName(anyString())).thenReturn(mock(Player.class));

        Map<String,Double> stats = new HashMap<>();
        stats.put("PTS", null);

        when(playerDAO.getAggregatedMetrics(anyString(),anyInt(),anyInt(),any(),any()))
                .thenReturn(stats);

        CompareInputData input = new CompareInputData(
                CompareInputData.EntityType.PLAYER,
                List.of("A", "B"),
                2020, 2020,
                "Basic", null
        );

        interactor.execute(input);

        verify(presenter).present(any());
    }

    @Test
    void testTieValuesDoesNotChangeBestIndex() {
        when(playerDAO.getPlayerByName(anyString())).thenReturn(mock(Player.class));

        Map<String,Double> p1 = Map.of("PTS", 10.0);
        Map<String,Double> p2 = Map.of("PTS", 10.0);

        when(playerDAO.getAggregatedMetrics(eq("A"),anyInt(),anyInt(),any(),any())).thenReturn(p1);
        when(playerDAO.getAggregatedMetrics(eq("B"),anyInt(),anyInt(),any(),any())).thenReturn(p2);

        CompareInputData input = new CompareInputData(
                CompareInputData.EntityType.PLAYER,
                List.of("A","B"),
                2020, 2020,
                "Basic", null
        );

        interactor.execute(input);

        verify(presenter).present(argThat(out ->
                out.table().get(0).bestIndex() == 0
        ));
    }

    @Test
    void testUnexpectedExceptionStillAddsNotice() {
        when(playerDAO.getPlayerByName("A")).thenReturn(mock(Player.class));
        when(playerDAO.getAggregatedMetrics(eq("A"), anyInt(), anyInt(), any(), any()))
                .thenReturn(Map.of("PTS", 20.0));

        when(playerDAO.getPlayerByName("B")).thenReturn(mock(Player.class));
        when(playerDAO.getAggregatedMetrics(eq("B"), anyInt(), anyInt(), any(), any()))
                .thenThrow(new RuntimeException("DB read failure"));

        CompareInputData input = new CompareInputData(
                CompareInputData.EntityType.PLAYER,
                List.of("A", "B"),
                2020, 2020,
                "Basic",
                null
        );

        interactor.execute(input);

        verify(presenter).present(argThat(out ->
                out.notices().contains("Could not load data for B.")
        ));
    }

    @Test
    void testNoMetricsPresetStillPresentsOutput() {
        when(playerDAO.getPlayerByName(anyString())).thenReturn(mock(Player.class));
        when(playerDAO.getAggregatedMetrics(anyString(), anyInt(), anyInt(), any(), any()))
                .thenReturn(Map.of("PTS", 12.0));

        CompareInputData input = new CompareInputData(
                CompareInputData.EntityType.PLAYER,
                List.of("A","B"),
                2020, 2020,
                "UnknownPreset",
                null
        );

        interactor.execute(input);

        verify(presenter).present(argThat(out ->
                out.table().size() == 0
        ));
    }

    @Test
    void testSuccessfulTeamCompare() {

        Map<String, Double> lakers = Map.of(
                "PTS", 110.0,
                "TRB", 45.0,
                "AST", 25.0,
                "STL", 7.0,
                "BLK", 5.0
        );
        Map<String, Double> celtics = Map.of(
                "PTS", 105.0,
                "TRB", 43.0,
                "AST", 24.0,
                "STL", 6.0,
                "BLK", 4.0
        );


        when(teamDAO.getTeamByName("Lakers")).thenReturn(mock(Team.class));
        when(teamDAO.getTeamByName("Celtics")).thenReturn(mock(Team.class));

        when(teamDAO.getAggregatedMetrics(eq("Lakers"), anyInt(), anyInt(), any(), any()))
                .thenReturn(lakers);
        when(teamDAO.getAggregatedMetrics(eq("Celtics"), anyInt(), anyInt(), any(), any()))
                .thenReturn(celtics);

        CompareInputData input = new CompareInputData(
                CompareInputData.EntityType.TEAM,
                List.of("Lakers", "Celtics"),
                2020,
                2020,
                "Basic",
                null
        );

        interactor.execute(input);

        verify(presenter).present(argThat(out ->
                out.entities().equals(List.of("Lakers", "Celtics")) &&
                        out.table().size() == 5 &&
                        out.table().get(0).values().size() == 2
        ));
    }

    @Test
    void testUnknownPresetDefaultsToBasic() {

        when(playerDAO.getPlayerByName(anyString())).thenReturn(mock(Player.class));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<String>> metricsCaptor = ArgumentCaptor.forClass(List.class);

        when(playerDAO.getAggregatedMetrics(
                anyString(),
                anyInt(),
                anyInt(),
                any(),
                metricsCaptor.capture()
        )).thenReturn(Map.of(
                "PTS", 10.0,
                "TRB", 5.0,
                "AST", 3.0,
                "STL", 1.0,
                "BLK", 1.0
        ));

        CompareInputData input = new CompareInputData(
                CompareInputData.EntityType.PLAYER,
                List.of("A", "B"),
                2020,
                2020,
                "WeirdPreset",
                null
        );

        interactor.execute(input);

        verify(presenter).present(any());

        List<List<String>> allMetricCalls = metricsCaptor.getAllValues();
        assertFalse(allMetricCalls.isEmpty());

        List<String> usedMetrics = allMetricCalls.get(0);
        assertEquals(List.of("PTS", "TRB", "AST", "STL", "BLK"), usedMetrics);
    }

    @Test
    void testNullInputListTriggersError() {
        CompareInputData input = new CompareInputData(
                CompareInputData.EntityType.PLAYER,
                null,
                2020,
                2021,
                "Basic",
                null
        );

        interactor.execute(input);

        verify(presenter).presentError("Select at least two players or teams.");
    }





}

