package use_case.search_player;

import entity.Player;

public class SearchPlayerOutputData {
    private final Player player;

    public SearchPlayerOutputData(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}

