package use_case.player_search;

import entity.Player;

public class PlayerSearchOutputData {
    private final Player player;
    private final boolean useCaseFailed;

    public PlayerSearchOutputData(Player player, boolean useCaseFailed) {
        this.player = player;
        this.useCaseFailed = useCaseFailed;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isUseCaseFailed() {
        return useCaseFailed;
    }
}
