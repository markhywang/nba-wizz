package app;

import data_access.CsvPlayerDataAccessObject;
import data_access.PlayerDataAccessInterface;
import interface_adapter.ViewManagerModel;
import interface_adapter.player_search.PlayerSearchController;
import interface_adapter.player_search.PlayerSearchPresenter;
import interface_adapter.player_search.PlayerSearchViewModel;
import use_case.player_search.PlayerSearchInputBoundary;
import use_case.player_search.PlayerSearchInteractor;
import use_case.player_search.PlayerSearchOutputBoundary;
import view.PlayerSearchView;
import view.ViewManager;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        JFrame application = new JFrame("NBA Wizz");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        CardLayout cardLayout = new CardLayout();
        JPanel views = new JPanel(cardLayout);
        application.add(views);

        ViewManagerModel viewManagerModel = new ViewManagerModel();
        new ViewManager(views, cardLayout);

        PlayerSearchViewModel playerSearchViewModel = new PlayerSearchViewModel();

        // The data access object.
        // TODO: Update the path to the CSV file.
        PlayerDataAccessInterface playerDataAccessObject = new CsvPlayerDataAccessObject("PlayerStatsDataset.csv");

        PlayerSearchOutputBoundary playerSearchOutputBoundary = new PlayerSearchPresenter(playerSearchViewModel, viewManagerModel);
        PlayerSearchInputBoundary playerSearchInteractor = new PlayerSearchInteractor(playerDataAccessObject, playerSearchOutputBoundary);
        PlayerSearchController playerSearchController = new PlayerSearchController(playerSearchInteractor);

        PlayerSearchView playerSearchView = new PlayerSearchView(playerSearchViewModel, playerSearchController);
        views.add(playerSearchView, playerSearchView.viewName);

        viewManagerModel.setActiveView(playerSearchView.viewName);
        viewManagerModel.firePropertyChanged();

        application.pack();
        application.setVisible(true);
    }
}
