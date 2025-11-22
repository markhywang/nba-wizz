package use_case.compare;

import data_access.PlayerDataAccessInterface;
import data_access.TeamDataAccessInterface;
import entity.Normalization;
import entity.Player;
import entity.Team;
import org.junit.Before;
import org.junit.Test;
import use_case.compare.CompareInputData;
import use_case.compare.CompareInteractor;
import use_case.compare.CompareOutputBoundary;
import use_case.compare.CompareOutputData;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests for CompareInteractor following project test formatting.
 */
public class CompareInteractorTest {

    private PlayerDataAccessInterface playerDAO;
    private TeamDataAccessInterface teamDAO;
    private TestPresenter presenter;
    private CompareInteractor interactor;

    @Before
    public void setup() {
        playerDAO = mock(PlayerDataAccessInterface.class);
        teamDAO = mock(TeamDataAccessInterface.class);
        presenter = new TestPresenter();

        interactor = new CompareInteractor(playerDAO, teamDAO, presenter);
    }

    @Test
    public void comparePlayers_basicPerGame_success() {

        List<String> players = List.of("Larry Bird", "Magic Johnson");

        // mock existence checks
        when(playerDAO.getPlayerByName("Larry Bird")).thenReturn(mock(Player.class));
        when(playerDAO.getPlayerByName("Magic Johnson")).thenReturn(mock(Player.class));

        // mock aggregated stats
        when(playerDAO.getAggregatedMetrics(eq("Larry Bird"), eq(1980), eq(1985),
                eq(Normalization.PER_GAME), anyList()))
                .thenReturn(Map.of(
                        "PTS", 25.0,
                        "TRB", 10.0,
                        "AST", 7.0
                ));

        when(playerDAO.getAggregatedMetrics(eq("Magic Johnson"), eq(1980), eq(1985),
                eq(Normalization.PER_GAME), anyList()))
                .thenReturn(Map.of(
                        "PTS", 20.0,
                        "TRB", 8.0,
                        "AST", 11.0
                ));

        CompareInputData request = new CompareInputData(
                CompareInputData.EntityType.PLAYER,
                players,
                1980,
                1985,
                "Basic",
                Normalization.PER_GAME
        );

        interactor.execute(request);

        assertNull(presenter.errorMessage);
        CompareOutputData output = presenter.output;

        assertEquals(players, output.getEntities());
        assertEquals("1980-1985", output.getSeasonLabel());

        // check best index picked correctly
        CompareOutputData.Row ptsRow = output.getTable().get(0);
        assertEquals("PTS", ptsRow.metric);
        assertEquals(Integer.valueOf(0), ptsRow.bestIndex);

        CompareOutputData.Row astRow =
                output.getTable().stream().filter(r -> r.metric.equals("AST")).findFirst().get();
        assertEquals(Integer.valueOf(1), astRow.bestIndex);
    }

    // ----------------------------------------------------------------------
    // ERROR CHECKING
    // ----------------------------------------------------------------------

    @Test
    public void error_whenOnlyOneEntity() {

        CompareInputData request = new CompareInputData(
                CompareInputData.EntityType.PLAYER,
                List.of("Larry Bird"),
                1980,
                1985,
                "Basic",
                Normalization.PER_GAME
        );

        interactor.execute(request);

        assertEquals("Select at least two players or teams.", presenter.errorMessage);
        assertNull(presenter.output);
    }

    @Test
    public void error_whenTooManyEntities() {

        CompareInputData request = new CompareInputData(
                CompareInputData.EntityType.PLAYER,
                List.of("A", "B", "C", "D", "E", "F"),
                1980,
                1985,
                "Basic",
                Normalization.PER_GAME
        );

        interactor.execute(request);

        assertEquals("You can compare at most five.", presenter.errorMessage);
        assertNull(presenter.output);
    }

    @Test
    public void error_whenSeasonStartGreaterThanEnd() {

        CompareInputData request = new CompareInputData(
                CompareInputData.EntityType.PLAYER,
                List.of("Larry Bird", "Magic Johnson"),
                1990, 1985,
                "Basic",
                Normalization.PER_GAME
        );

        interactor.execute(request);

        assertEquals("Season start must be <= season end.", presenter.errorMessage);
        assertNull(presenter.output);
    }

    @Test
    public void error_whenPlayerNotFound() {

        when(playerDAO.getPlayerByName("Larry Bird")).thenReturn(mock(Player.class));
        when(playerDAO.getPlayerByName("Unknown")).thenReturn(null);

        CompareInputData request = new CompareInputData(
                CompareInputData.EntityType.PLAYER,
                List.of("Larry Bird", "Unknown"),
                1980, 1985,
                "Basic",
                Normalization.PER_GAME
        );

        interactor.execute(request);

        assertEquals("Player 'Unknown' does not exist.", presenter.errorMessage);
        assertNull(presenter.output);
    }

    @Test
    public void error_whenTeamNotFound() {
        when(teamDAO.getTeamByName("bos")).thenReturn(mock(Team.class));
        when(teamDAO.getTeamByName("lal")).thenReturn(null);

        CompareInputData request = new CompareInputData(
                CompareInputData.EntityType.TEAM,
                List.of("bos", "lal"),
                1980, 1985,
                "Basic",
                Normalization.PER_GAME
        );

        interactor.execute(request);

        assertEquals("Team 'lal' does not exist.", presenter.errorMessage);
        assertNull(presenter.output);
    }

    // ----------------------------------------------------------------------
    // TEST PRESENTER
    // ----------------------------------------------------------------------

    private static class TestPresenter implements CompareOutputBoundary {

        private CompareOutputData output;
        private String errorMessage;

        @Override
        public void present(CompareOutputData response) {
            this.output = response;
            this.errorMessage = null;
        }

        @Override
        public void presentError(String message) {
            this.errorMessage = message;
            this.output = null;
        }

        @Override
        public void switchToMainMenu() {
            // not needed in tests
        }
    }
}
