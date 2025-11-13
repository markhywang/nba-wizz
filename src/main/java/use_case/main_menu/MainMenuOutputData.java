package use_case.main_menu;

import entity.Player;

public class MainMenuOutputData {
    private Player player;
    private boolean useCaseFailed;

    public MainMenuOutputData(Player player, boolean useCaseFailed) {
        this.player = player;
        this.useCaseFailed = useCaseFailed;
    }

    public MainMenuOutputData() {
        this.player = null;
        this.useCaseFailed = false;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isUseCaseFailed() {
        return useCaseFailed;
    }
}
