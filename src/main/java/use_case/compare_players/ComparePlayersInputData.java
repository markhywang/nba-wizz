package use_case.compare_players;

public class ComparePlayersInputData {
    private final String player1Name;
    private final String player2Name;

    public ComparePlayersInputData(String player1Name, String player2Name) {
        this.player1Name = player1Name;
        this.player2Name = player2Name;
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public String getPlayer2Name() {
        return player2Name;
    }
}
