package interface_adapter.main_menu;

import interface_adapter.ViewManagerModel;
import interface_adapter.favourite.FavouriteController;
import use_case.main_menu.MainMenuInputBoundary;


public class MainMenuController {
    private final MainMenuInputBoundary interactor;
    private final ViewManagerModel viewManagerModel;
    private final FavouriteController favouriteController;

    public MainMenuController(MainMenuInputBoundary interactor,
                              ViewManagerModel viewManagerModel,
                              FavouriteController favouriteController) {
        this.interactor = interactor;
        this.viewManagerModel = viewManagerModel;
        this.favouriteController = favouriteController;
    }

    public void onSearchPlayerPressed() {
        interactor.switchToSearchPlayer();
    }

    public void switchToGenerateInsights() {
        interactor.switchToGenerateInsights();
    }

    public void switchToChat() {
        interactor.switchToChat();
    }

    public void onCompareButtonClicked() {
        viewManagerModel.setActiveView("compare");
        viewManagerModel.firePropertyChanged();
    }

    public void onFilterAndSortButtonClicked() {
        viewManagerModel.setActiveView("sort_players");
        viewManagerModel.firePropertyChanged();
    }

    public void onViewFavouritedPlayersPressed() {
        viewManagerModel.setActiveView("favourited_players");
        viewManagerModel.firePropertyChanged();
    }

}
