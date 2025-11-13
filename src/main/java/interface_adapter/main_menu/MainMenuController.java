package interface_adapter.main_menu;

import interface_adapter.ViewManagerModel;
import use_case.main_menu.MainMenuInputBoundary;
import use_case.main_menu.MainMenuInputData;

public class MainMenuController {
    private final MainMenuInputBoundary interactor;
    private final ViewManagerModel viewManagerModel;

    public MainMenuController(MainMenuInputBoundary interactor,
                              ViewManagerModel viewManagerModel) {
        this.interactor = interactor;
        this.viewManagerModel = viewManagerModel;
    }

<<<<<<< HEAD
    public void onSearchPlayerPressed() {
        viewManagerModel.setActiveView("search_player");
        viewManagerModel.firePropertyChanged();
    }
}
=======
    public void execute(String button) {
        MainMenuInputData mainMenuInputData = new MainMenuInputData(button);
        mainMenuInteractor.execute(mainMenuInputData);
    }

    public void switchToGenerateInsights() {
        mainMenuInteractor.switchToGenerateInsights();
    }
}
>>>>>>> ea40270 (Added initial AI insights backend)
