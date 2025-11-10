package use_case.player_search;

public class PlayerSearchInputData {
    private final String playerName;

    public PlayerSearchInputData(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }
}
