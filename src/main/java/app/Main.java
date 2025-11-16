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
import interface_adapter.ask_question.AskQuestionController;
import interface_adapter.ask_question.AskQuestionPresenter;
import interface_adapter.ask_question.AskQuestionViewModel;
import interface_adapter.compare_players.ComparePlayersController;
import interface_adapter.compare_players.ComparePlayersPresenter;
import interface_adapter.compare_players.ComparePlayersViewModel;
import use_case.ask_question.AskQuestionInputBoundary;
import use_case.ask_question.AskQuestionInteractor;
import use_case.compare_players.ComparePlayersInputBoundary;
import use_case.compare_players.ComparePlayersInteractor;
import view.ChatView;


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
        AskQuestionViewModel askQuestionViewModel = new AskQuestionViewModel();
        ComparePlayersViewModel comparePlayersViewModel = new ComparePlayersViewModel();


        // The data access object.
        // TODO: Update the path to the CSV file.
        PlayerDataAccessInterface playerDataAccessObject = new CsvPlayerDataAccessObject("PlayerStatsDataset.csv");
        GeminiDataAccessObject geminiDataAccessObject = new GeminiDataAccessObject();

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

        AskQuestionPresenter askQuestionPresenter = new AskQuestionPresenter(askQuestionViewModel, viewManagerModel);
        AskQuestionInputBoundary askQuestionInteractor = new AskQuestionInteractor(geminiDataAccessObject, askQuestionPresenter);
        AskQuestionController askQuestionController = new AskQuestionController(askQuestionInteractor);

        ComparePlayersPresenter comparePlayersPresenter = new ComparePlayersPresenter(comparePlayersViewModel, viewManagerModel);
        ComparePlayersInputBoundary comparePlayersInteractor = new ComparePlayersInteractor(geminiDataAccessObject, comparePlayersPresenter);
        ComparePlayersController comparePlayersController = new ComparePlayersController(comparePlayersInteractor);

        ChatView chatView = new ChatView(askQuestionViewModel, askQuestionController, comparePlayersViewModel, comparePlayersController);
        views.add(chatView, chatView.viewName);

        application.pack();
        application.setVisible(true);
    }
}