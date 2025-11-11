package use_case.main_menu;

import entity.Player;

public class MainMenuOutputData {
    private final Player player;
    private final boolean useCaseFailed;

    public MainMenuOutputData(Player player, boolean useCaseFailed) {
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
