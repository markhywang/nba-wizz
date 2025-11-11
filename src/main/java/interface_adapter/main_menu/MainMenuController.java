package interface_adapter.main_menu;

import use_case.main_menu.MainMenuInputBoundary;
import use_case.main_menu.MainMenuInputData;

public class MainMenuController {
    private final MainMenuInputBoundary mainMenuInteractor;

    public MainMenuController(MainMenuInputBoundary mainMenuInteractor) {
        this.mainMenuInteractor = mainMenuInteractor;
    }

    public void execute(String playerName) {
        MainMenuInputData mainMenuInputData = new MainMenuInputData(playerName);
        mainMenuInteractor.execute(mainMenuInputData);
    }
}
