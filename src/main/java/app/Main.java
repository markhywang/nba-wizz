package app;

import data_access.CsvPlayerDataAccessObject;
import data_access.PlayerDataAccessInterface;
import interface_adapter.ViewManagerModel;
import interface_adapter.main_menu.MainMenuController;
import interface_adapter.main_menu.MainMenuPresenter;
import interface_adapter.main_menu.MainMenuViewModel;
import interface_adapter.search_player.SearchPlayerController;
import interface_adapter.search_player.SearchPlayerPresenter;
import interface_adapter.search_player.SearchPlayerViewModel;
import use_case.main_menu.MainMenuInputBoundary;
import use_case.main_menu.MainMenuInteractor;
import use_case.main_menu.MainMenuOutputBoundary;
import use_case.search_player.SearchPlayerInputBoundary;
import use_case.search_player.SearchPlayerInteractor;
import use_case.search_player.SearchPlayerOutputBoundary;
import view.MainMenuView;
import view.SearchPlayerView;
import view.ViewManager;
import interface_adapter.generate_insights.GenerateInsightsViewModel;
import interface_adapter.generate_insights.GenerateInsightsController;
import interface_adapter.generate_insights.GenerateInsightsPresenter;
import data_access.GeminiDataAccessObject;
import use_case.generate_insights.GenerateInsightsDataAccessInterface;
import use_case.generate_insights.GenerateInsightsInputBoundary;
import use_case.generate_insights.GenerateInsightsInteractor;
import use_case.generate_insights.GenerateInsightsOutputBoundary;
import view.GenerateInsightsView;


import javax.swing.*;
import java.awt.*;

/*Run this file to run NBA Wizz*/
public class Main {
    public static void main(String[] args) {
        JFrame application = new JFrame("NBA Wizz");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        CardLayout cardLayout = new CardLayout();
        JPanel views = new JPanel(cardLayout);
        application.add(views);

        ViewManagerModel viewManagerModel = new ViewManagerModel();
        new ViewManager(views, cardLayout, viewManagerModel);

        MainMenuViewModel mainMenuViewModel = new MainMenuViewModel();
        GenerateInsightsViewModel generateInsightsViewModel = new GenerateInsightsViewModel();

        // The data access object.
        // TODO: Update the path to the CSV file.
        PlayerDataAccessInterface playerDataAccessObject = new CsvPlayerDataAccessObject("PlayerStatsDataset.csv");
        GenerateInsightsDataAccessInterface geminiDataAccessObject = new GeminiDataAccessObject();

        MainMenuOutputBoundary mainMenuPresenter = new MainMenuPresenter(mainMenuViewModel, viewManagerModel, generateInsightsViewModel);
        MainMenuInputBoundary mainMenuInteractor = new MainMenuInteractor(playerDataAccessObject, mainMenuPresenter);
        MainMenuController mainMenuController = new MainMenuController(mainMenuInteractor, viewManagerModel);
        MainMenuView mainMenuView = new MainMenuView(mainMenuViewModel, mainMenuController);
        views.add(mainMenuView, mainMenuView.viewName);

        GenerateInsightsOutputBoundary generateInsightsOutputBoundary = new GenerateInsightsPresenter(generateInsightsViewModel, viewManagerModel);
        GenerateInsightsInputBoundary generateInsightsInteractor = new GenerateInsightsInteractor(geminiDataAccessObject, generateInsightsOutputBoundary);
        GenerateInsightsController generateInsightsController = new GenerateInsightsController(generateInsightsInteractor, viewManagerModel, mainMenuViewModel);

        GenerateInsightsView generateInsightsView = new GenerateInsightsView(generateInsightsViewModel, generateInsightsController);
        views.add(generateInsightsView, generateInsightsView.viewName);


        viewManagerModel.setActiveView(mainMenuView.viewName);
        viewManagerModel.firePropertyChanged();

        /*Search Player Feature Setup*/
        SearchPlayerViewModel searchPlayerViewModel = new SearchPlayerViewModel();
        searchPlayerViewModel.setViewManagerModel(viewManagerModel);

        SearchPlayerOutputBoundary searchPlayerPresenter =
                new SearchPlayerPresenter(searchPlayerViewModel, viewManagerModel);

        SearchPlayerInputBoundary searchPlayerInteractor =
                new SearchPlayerInteractor(playerDataAccessObject, searchPlayerPresenter);

        SearchPlayerController searchPlayerController =
                new SearchPlayerController(searchPlayerInteractor);

        SearchPlayerView searchPlayerView =
                new SearchPlayerView(searchPlayerController, searchPlayerViewModel);

        views.add(searchPlayerView, searchPlayerView.viewName);

        application.pack();
        application.setVisible(true);
    }
}