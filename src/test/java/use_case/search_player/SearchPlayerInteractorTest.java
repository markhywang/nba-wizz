package use_case.search_player;

import data_access.PlayerDataAccessInterface;
import entity.Normalization;
import entity.Player;
import entity.SeasonStats;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**Unit tests for SearchPlayerInteractor with 100% coverage*/
class SearchPlayerInteractorTest {

    // MOCK DAO
    static class MockPlayerDAO implements PlayerDataAccessInterface {
        Player playerToReturn;
        String lastReceivedName;

        @Override
        public Player getPlayerByName(String name) {
            this.lastReceivedName = name;
            return playerToReturn;
        }

        @Override
        public List<Player> findByTeam(String teamName) {
            return List.of();
        }

        @Override
        public List<Player> findByPosition(String position) {
            return List.of();
        }

        @Override
        public List<Player> findBySeason(int seasonYear) {
            return List.of();
        }

        @Override
        public Map<String, Double> getAggregatedMetrics(
                String playerName,
                int seasonStartInclusive,
                int seasonEndInclusive,
                Normalization normalization,
                List<String> metrics) {
            return Map.of();
        }

        @Override
        public Map<String, Double> getAggregateMetrics(
                String playerName,
                int seasonStartInclusive,
                int seasonEndInclusive,
                Normalization normalization,
                List<String> metrics) {
            return Map.of();
        }

        @Override
        public void save(Player entity) {
            // not needed for this test
        }

        @Override
        public Optional<Player> findById(int id) {
            return Optional.empty();
        }

        @Override
        public List<Player> findAll() {
            return List.of();
        }

        @Override
        public void update(Player entity) {
            // not needed for this test
        }

        @Override
        public void delete(Player entity) {
            // not needed for this test
        }
    }

    // MOCK PRESENTER
    static class MockPresenter implements SearchPlayerOutputBoundary {
        boolean presentCalled = false;
        boolean presentNotFoundCalled = false;
        SearchPlayerOutputData receivedOutput = null;
        String receivedMessage = null;

        @Override
        public void present(SearchPlayerOutputData outputData) {
            presentCalled = true;
            receivedOutput = outputData;
        }

        @Override
        public void presentPlayerNotFound(String message) {
            presentNotFoundCalled = true;
            receivedMessage = message;
        }
    }

    //TEST: player exists with valid input
    @Test
    void playerFoundValidInputTest() {
        MockPlayerDAO dao = new MockPlayerDAO();
        MockPresenter presenter = new MockPresenter();

        dao.playerToReturn = new Player(
                23,
                "LeBron James",
                null,
                "Forward",
                39,
                2.03,
                250.0,
                List.<SeasonStats>of()
        );

        SearchPlayerInteractor interactor =
                new SearchPlayerInteractor(dao, presenter);

        SearchPlayerInputData input = new SearchPlayerInputData(
                "LeBron James",
                "2010",
                "2015",
                List.of("PPG", "APG")
        );

        interactor.execute(input);

        assertEquals("LeBron James", dao.lastReceivedName);

        assertTrue(presenter.presentCalled);
        assertFalse(presenter.presentNotFoundCalled);

        assertNotNull(presenter.receivedOutput);

        assertNotNull(presenter.receivedOutput.getTableRows());
        assertNotNull(presenter.receivedOutput.getGraphData());
    }

    // TEST: player not found
    @Test
    void playerNotFoundTest() {
        MockPlayerDAO dao = new MockPlayerDAO();
        MockPresenter presenter = new MockPresenter();

        dao.playerToReturn = null;

        SearchPlayerInteractor interactor =
                new SearchPlayerInteractor(dao, presenter);

        SearchPlayerInputData input = new SearchPlayerInputData(
                "Unknown Player",
                "2010",
                "2015",
                List.of("PPG")
        );

        interactor.execute(input);

        assertEquals("Unknown Player", dao.lastReceivedName);
        assertTrue(presenter.presentNotFoundCalled);
        assertFalse(presenter.presentCalled);
        assertEquals("Player not found. Re-enter a valid player name.", presenter.receivedMessage);
    }

    // TEST: empty season fields
    @Test
    void emptySeasonFieldsTest() {
        MockPlayerDAO dao = new MockPlayerDAO();
        MockPresenter presenter = new MockPresenter();

        SearchPlayerInteractor interactor =
                new SearchPlayerInteractor(dao, presenter);

        SearchPlayerInputData input = new SearchPlayerInputData(
                "LeBron James",
                "",
                "2015",
                List.of("PPG")
        );

        interactor.execute(input);

        assertTrue(presenter.presentNotFoundCalled);
        assertFalse(presenter.presentCalled);
        assertEquals("Season fields cannot be empty.", presenter.receivedMessage);
    }

    // TEST: non-numeric seasons
    @Test
    void nonNumericSeasonTest() {
        MockPlayerDAO dao = new MockPlayerDAO();
        MockPresenter presenter = new MockPresenter();

        SearchPlayerInteractor interactor =
                new SearchPlayerInteractor(dao, presenter);

        SearchPlayerInputData input = new SearchPlayerInputData(
                "LeBron James",
                "20A0",
                "2015",
                List.of("PPG")
        );

        interactor.execute(input);

        assertTrue(presenter.presentNotFoundCalled);
        assertFalse(presenter.presentCalled);
        assertEquals("Season fields must be numbers only.", presenter.receivedMessage);
    }

    //TEST: season out of allowed range
    @Test
    void seasonOutOfRangeTest() {
        MockPlayerDAO dao = new MockPlayerDAO();
        MockPresenter presenter = new MockPresenter();

        dao.playerToReturn = new Player(
                23,
                "LeBron James",
                null,
                "Forward",
                39,
                2.03,
                250.0,
                List.<SeasonStats>of()
        );

        SearchPlayerInteractor interactor =
                new SearchPlayerInteractor(dao, presenter);

        SearchPlayerInputData input = new SearchPlayerInputData(
                "LeBron James",
                "1975",
                "1985",
                List.of("PPG")
        );

        interactor.execute(input);

        assertTrue(presenter.presentNotFoundCalled);
        assertFalse(presenter.presentCalled);
        assertEquals("Season range must be between 1980 and 2024", presenter.receivedMessage);
    }

    // TEST: start season after end season
    @Test
    void startAfterEndSeasonTest() {
        MockPlayerDAO dao = new MockPlayerDAO();
        MockPresenter presenter = new MockPresenter();

        dao.playerToReturn = new Player(
                23,
                "LeBron James",
                null,
                "Forward",
                39,
                2.03,
                250.0,
                List.<SeasonStats>of()
        );

        SearchPlayerInteractor interactor =
                new SearchPlayerInteractor(dao, presenter);

        SearchPlayerInputData input = new SearchPlayerInputData(
                "LeBron James",
                "2020",
                "2018",
                List.of("PPG")
        );

        interactor.execute(input);

        assertTrue(presenter.presentNotFoundCalled);
        assertFalse(presenter.presentCalled);
        assertEquals("Start season cannot be after end season", presenter.receivedMessage);
    }
    @Test
    void tableAndGraphDataTest() {

        // Mock DAO
        MockPlayerDAO dao = new MockPlayerDAO();
        MockPresenter presenter = new MockPresenter();

        Player fakePlayer = new Player(1, "Test Player", null, "G", 28, 1.9,
                200, new ArrayList<>());

        SeasonStats s1 = new SeasonStats(2020,25.0,7.0,8.0,
                0.50,60,35.0, 0.00, fakePlayer);

        fakePlayer.getCareerStats().add(s1);
        dao.playerToReturn = fakePlayer;

        SearchPlayerInteractor interactor =
                new SearchPlayerInteractor(dao, presenter);

        SearchPlayerInputData input = new SearchPlayerInputData(
                "Test Player",
                "2020",
                "2020",
                List.of("PPG", "APG")
        );

        interactor.execute(input);

        assertTrue(presenter.presentCalled);
        assertNotNull(presenter.receivedOutput);

        List<String[]> table = presenter.receivedOutput.getTableRows();
        assertEquals(1, table.size());

        assertArrayEquals(
                new String[]{"2020", "25.0", "7.0", "8.0", "0.5"},
                table.get(0)
        );

        Map<String, Map<Integer, Double>> graph = presenter.receivedOutput.getGraphData();

        assertTrue(graph.containsKey("PPG"));
        assertTrue(graph.containsKey("APG"));

        assertEquals(25.0, graph.get("PPG").get(2020));
        assertEquals(7.0, graph.get("APG").get(2020));
    }

    @Test
    void emptySelectedStatsStillWorksTest() {
        MockPlayerDAO dao = new MockPlayerDAO();
        MockPresenter presenter = new MockPresenter();

        SeasonStats s1 = new SeasonStats(2020, 25.0, 7.0, 8.0,
                0.50, 60, 35.0, 0.00, null);

        dao.playerToReturn = new Player(1, "Test Player", null, "G", 28, 1.9,
                200, List.of(s1));

        SearchPlayerInteractor interactor =
                new SearchPlayerInteractor(dao, presenter);

        SearchPlayerInputData input = new SearchPlayerInputData(
                "Test Player",
                "2020",
                "2020",
                List.of()
        );

        interactor.execute(input);

        assertTrue(presenter.presentCalled);

        assertEquals(1, presenter.receivedOutput.getTableRows().size());

        assertTrue(presenter.receivedOutput.getGraphData().isEmpty());
    }

    @Test
    void playerWithNoCareerStatsTest() {
        MockPlayerDAO dao = new MockPlayerDAO();
        MockPresenter presenter = new MockPresenter();

        dao.playerToReturn = new Player(1, "Empty Player", null, "G", 28, 1.9,
                200, new ArrayList<>());

        SearchPlayerInteractor interactor =
                new SearchPlayerInteractor(dao, presenter);

        SearchPlayerInputData input = new SearchPlayerInputData(
                "Empty Player",
                "2020",
                "2021",
                List.of("PPG", "APG")
        );

        interactor.execute(input);

        assertTrue(presenter.presentCalled);

        assertEquals(0, presenter.receivedOutput.getTableRows().size());

        assertTrue(presenter.receivedOutput.getGraphData().get("PPG").isEmpty());
        assertTrue(presenter.receivedOutput.getGraphData().get("APG").isEmpty());
    }
}
