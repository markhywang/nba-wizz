package interface_adapter.player_search;

public class PlayerSearchState {
    private String error = null;

    public PlayerSearchState(PlayerSearchState copy) {
        error = copy.error;
    }

    public PlayerSearchState() {}

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
