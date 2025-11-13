package app;

import data_access.CsvPlayerDataAccessObject;
import data_access.PlayerDataAccessInterface;
import interface_adapter.ViewManagerModel;
import interface_adapter.main_menu.MainMenuController;
import interface_adapter.main_menu.MainMenuPresenter;
import interface_adapter.main_menu.MainMenuViewModel;
import use_case.main_menu.MainMenuInputBoundary;
import use_case.main_menu.MainMenuInteractor;
import use_case.main_menu.MainMenuOutputBoundary;
import view.MainMenuView;
import view.ViewManager;
import interface_adapter.generate_insights.GenerateInsightsViewModel;
import interface_adapter.generate_insights.GenerateInsightsController;
import interface_adapter.generate_insights.GenerateInsightsPresenter;
import data_access.OllamaDataAccessObject;
import data_access.GenerateInsightsDataAccessInterface;
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
        GenerateInsightsDataAccessInterface ollamaDataAccessObject = new OllamaDataAccessObject();

        MainMenuOutputBoundary mainMenuOutputBoundary = new MainMenuPresenter(mainMenuViewModel, viewManagerModel, generateInsightsViewModel);
        MainMenuInputBoundary playerSearchInteractor = new MainMenuInteractor(playerDataAccessObject, mainMenuOutputBoundary);
        MainMenuController mainMenuController = new MainMenuController(playerSearchInteractor);

        MainMenuView mainMenuView = new MainMenuView(mainMenuViewModel, mainMenuController);
        views.add(mainMenuView, mainMenuView.viewName);

        GenerateInsightsOutputBoundary generateInsightsOutputBoundary = new GenerateInsightsPresenter(generateInsightsViewModel, viewManagerModel);
        GenerateInsightsInputBoundary generateInsightsInteractor = new GenerateInsightsInteractor(ollamaDataAccessObject, generateInsightsOutputBoundary);
        GenerateInsightsController generateInsightsController = new GenerateInsightsController(generateInsightsInteractor, viewManagerModel, mainMenuViewModel);

        GenerateInsightsView generateInsightsView = new GenerateInsightsView(generateInsightsViewModel, generateInsightsController);
        views.add(generateInsightsView, generateInsightsView.viewName);


        viewManagerModel.setActiveView(mainMenuView.viewName);
        viewManagerModel.firePropertyChanged();

        application.pack();
        application.setVisible(true);
    }
}
