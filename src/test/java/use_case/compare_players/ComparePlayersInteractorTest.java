package use_case.compare_players;

import entity.Answer;
import entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

class ComparePlayersInteractorTest {

    private ComparePlayersDataAccessInterface dataAccess;
    private ComparePlayersOutputBoundary presenter;
    private ComparePlayersInteractor interactor;

    @BeforeEach
    void setUp() {
        dataAccess = new MockComparePlayersDataAccess();
        presenter = new MockComparePlayersPresenter();
        interactor = new ComparePlayersInteractor(dataAccess, presenter);
    }

    @Test
    void execute_withValidPlayers_shouldPrepareSuccessView() {
        ComparePlayersInputData inputData = new ComparePlayersInputData("LeBron James", "Michael Jordan");
        interactor.execute(inputData);

        MockComparePlayersPresenter mockPresenter = (MockComparePlayersPresenter) presenter;
        assertTrue(mockPresenter.isSuccess());
        assertNotNull(mockPresenter.getOutputData());
        assertEquals("Comparison result", mockPresenter.getOutputData().getComparison().getResponse());
    }

    @Test
    void execute_withEmptyPlayerName_shouldPrepareFailView() {
        ComparePlayersInputData inputData = new ComparePlayersInputData(" ", "Michael Jordan");
        interactor.execute(inputData);

        MockComparePlayersPresenter mockPresenter = (MockComparePlayersPresenter) presenter;
        assertFalse(mockPresenter.isSuccess());
        assertEquals("Player names cannot be empty.", mockPresenter.getError());
    }

    @Test
    void execute_withPlayer1NotFound_shouldPrepareFailView() {
        dataAccess = new MockComparePlayersDataAccess() {
            @Override
            public Optional<Player> getPlayerByName(String playerName) {
                if (playerName.equals("NotFound")) return Optional.empty();
                return Optional.of(new Player(2, "Michael Jordan", null, "SG", 0, 0, 0, null));
            }
        };
        interactor = new ComparePlayersInteractor(dataAccess, presenter);
        ComparePlayersInputData inputData = new ComparePlayersInputData("NotFound", "Michael Jordan");
        interactor.execute(inputData);

        MockComparePlayersPresenter mockPresenter = (MockComparePlayersPresenter) presenter;
        assertFalse(mockPresenter.isSuccess());
        assertEquals("Player 1 not found.", mockPresenter.getError());
    }

    @Test
    void execute_withPlayer2NotFound_shouldPrepareFailView() {
        dataAccess = new MockComparePlayersDataAccess() {
            @Override
            public Optional<Player> getPlayerByName(String playerName) {
                if (playerName.equals("NotFound")) return Optional.empty();
                return Optional.of(new Player(1, "LeBron James", null, "SF", 0, 0, 0, null));
            }
        };
        interactor = new ComparePlayersInteractor(dataAccess, presenter);
        ComparePlayersInputData inputData = new ComparePlayersInputData("LeBron James", "NotFound");
        interactor.execute(inputData);

        MockComparePlayersPresenter mockPresenter = (MockComparePlayersPresenter) presenter;
        assertFalse(mockPresenter.isSuccess());
        assertEquals("Player 2 not found.", mockPresenter.getError());
    }

    // Mock implementations for testing
    private static class MockComparePlayersDataAccess implements ComparePlayersDataAccessInterface {
        @Override
        public Optional<Player> getPlayerByName(String playerName) {
            if (playerName.equals("LeBron James")) {
                return Optional.of(new Player(1, "LeBron James", null, "SF", 0, 0, 0, null));
            }
            if (playerName.equals("Michael Jordan")) {
                return Optional.of(new Player(2, "Michael Jordan", null, "SG", 0, 0, 0, null));
            }
            return Optional.empty();
        }

        @Override
        public String getPlayerComparison(Player player1, Player player2) {
            return "Comparison result";
        }
    }

    private static class MockComparePlayersPresenter implements ComparePlayersOutputBoundary {
        private boolean success = false;
        private String error = null;
        private ComparePlayersOutputData outputData;

        @Override
        public void prepareSuccessView(ComparePlayersOutputData outputData) {
            this.success = true;
            this.outputData = outputData;
        }

        @Override
        public void prepareFailView(String error) {
            this.success = false;
            this.error = error;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getError() {
            return error;
        }

        public ComparePlayersOutputData getOutputData() {
            return outputData;
        }
    }
}
