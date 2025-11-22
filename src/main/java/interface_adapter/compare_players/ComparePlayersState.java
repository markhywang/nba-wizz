package interface_adapter.compare_players;

public class ComparePlayersState {
    private String player1Name = "";
    private String player2Name = "";
    private String comparison = "";
    private String error = null;
    private boolean isLoading = false;

    public ComparePlayersState(ComparePlayersState copy) {
        this.player1Name = copy.player1Name;
        this.player2Name = copy.player2Name;
        this.comparison = copy.comparison;
        this.error = copy.error;
        this.isLoading = copy.isLoading;
    }

    public ComparePlayersState() {}

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public void setPlayer1Name(String player1Name) {
        this.player1Name = player1Name;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public void setPlayer2Name(String player2Name) {
        this.player2Name = player2Name;
    }

    public String getComparison() {
        return comparison;
    }

    public void setComparison(String comparison) {
        this.comparison = comparison;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
