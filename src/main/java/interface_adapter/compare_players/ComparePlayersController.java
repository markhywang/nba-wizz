package interface_adapter.compare_players;

import use_case.compare_players.ComparePlayersInputBoundary;
import use_case.compare_players.ComparePlayersInputData;

public class ComparePlayersController {
    private final ComparePlayersInputBoundary comparePlayersInteractor;

    public ComparePlayersController(ComparePlayersInputBoundary comparePlayersInteractor) {
        this.comparePlayersInteractor = comparePlayersInteractor;
    }

    public void execute(String player1Name, String player2Name) {
        ComparePlayersInputData inputData = new ComparePlayersInputData(player1Name, player2Name);
        comparePlayersInteractor.execute(inputData);
    }
}
