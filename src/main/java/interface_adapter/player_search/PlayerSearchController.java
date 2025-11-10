package interface_adapter.player_search;

import use_case.player_search.PlayerSearchInputBoundary;
import use_case.player_search.PlayerSearchInputData;

public class PlayerSearchController {
    private final PlayerSearchInputBoundary playerSearchInteractor;

    public PlayerSearchController(PlayerSearchInputBoundary playerSearchInteractor) {
        this.playerSearchInteractor = playerSearchInteractor;
    }

    public void execute(String playerName) {
        PlayerSearchInputData playerSearchInputData = new PlayerSearchInputData(playerName);
        playerSearchInteractor.execute(playerSearchInputData);
    }
}
