package interface_adapter.search_player;

public class SearchPlayerState {
    private String searchText = "";
    private String message = "";
    private String playerName = "";
    private String playerTeam = "";
    private String playerPosition = "";

    public String getSearchText() { return searchText; }
    public void setSearchText(String searchText) { this.searchText = searchText; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public String getPlayerTeam() { return playerTeam; }
    public void setPlayerTeam(String playerTeam) { this.playerTeam = playerTeam; }

    public String getPlayerPosition() { return playerPosition; }
    public void setPlayerPosition(String playerPosition) { this.playerPosition = playerPosition; }
}
