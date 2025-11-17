package app;

import data_access.CsvPlayerDataAccessObject;
import data_access.CsvTeamDataAccessObject;
import data_access.FavouriteDataAccessObject;
import data_access.FileUserDataAccessObject;
import data_access.PlayerDataAccessInterface;
import data_access.TeamDataAccessInterface;
import interface_adapter.ViewManagerModel;
import interface_adapter.auth.AuthController;
import interface_adapter.auth.AuthPresenter;
import interface_adapter.auth.AuthViewModel;
import interface_adapter.compare.CompareController;
import interface_adapter.compare.ComparePresenter;
import interface_adapter.compare.CompareViewModel;
import interface_adapter.favourite.FavouriteController;
import interface_adapter.favourite.FavouritePresenter;
import interface_adapter.favourite.FavouriteViewModel;
import interface_adapter.main_menu.MainMenuController;
import interface_adapter.main_menu.MainMenuPresenter;
import interface_adapter.main_menu.MainMenuViewModel;
import use_case.authentication.UserDataAccessInterface;
import use_case.authentication.login.LoginInputBoundary;
import use_case.authentication.login.LoginInteractor;
import use_case.authentication.signup.SignupInputBoundary;
import use_case.authentication.signup.SignupInteractor;
import use_case.compare.CompareInputBoundary;
import use_case.compare.CompareInteractor;
import use_case.compare.CompareOutputBoundary;
import interface_adapter.search_player.SearchPlayerController;
import interface_adapter.search_player.SearchPlayerPresenter;
import interface_adapter.search_player.SearchPlayerViewModel;
import use_case.favourite.FavouriteDataAccessInterface;
import use_case.favourite.FavouriteInputBoundary;
import use_case.favourite.FavouriteInteractor;
import use_case.favourite.FavouriteOutputData;
import use_case.main_menu.MainMenuInputBoundary;
import use_case.main_menu.MainMenuInteractor;
import use_case.main_menu.MainMenuOutputBoundary;
import use_case.search_player.SearchPlayerInputBoundary;
import use_case.search_player.SearchPlayerInteractor;
import use_case.search_player.SearchPlayerOutputBoundary;
import view.AuthView;
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
import view.FavoritedPlayersView;
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

import view.compare.CompareView;

import javax.swing.*;
import java.awt.*;

import interface_adapter.sort_players.SortViewModel;
import interface_adapter.sort_players.SortController;
import interface_adapter.sort_players.SortPresenter;
import use_case.sort.SortInputBoundary;
import use_case.sort.SortInteractor;
import use_case.sort.SortOutputBoundary;
import view.SortPlayersView;
import interface_adapter.filter_players.*;
import use_case.filter_players.*;
import view.FilterPlayersView;

/*Run this file to run NBA Wizz*/
public class Main {
    public static void main(String[] args) {
        JFrame application = new JFrame("NBA Wizz");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        CardLayout cardLayout = new CardLayout();
        JPanel views = new JPanel(cardLayout);
        application.add(views);

        // Favourites
        FavouriteViewModel favouriteViewModel = new FavouriteViewModel();
        FavouritePresenter favouritePresenter = new FavouritePresenter(favouriteViewModel);
        FavouriteDataAccessInterface favouriteDataAccessInterface = new FavouriteDataAccessObject();
        FavouriteInputBoundary favouriteInputBoundary = new FavouriteInteractor(favouritePresenter, favouriteDataAccessInterface);
        FavouriteController favouriteController = new FavouriteController(favouriteInputBoundary);
        // Initialize the favourite view model with existing favourites from storage
        favouritePresenter.addFavourite(new FavouriteOutputData(true, favouriteInputBoundary.getFavourites()));

        ViewManagerModel viewManagerModel = new ViewManagerModel();
        new ViewManager(views, cardLayout, viewManagerModel);

        MainMenuViewModel mainMenuViewModel = new MainMenuViewModel();
        GenerateInsightsViewModel generateInsightsViewModel = new GenerateInsightsViewModel();
        AskQuestionViewModel askQuestionViewModel = new AskQuestionViewModel();
        ComparePlayersViewModel comparePlayersViewModel = new ComparePlayersViewModel();
        AuthViewModel authViewModel = new AuthViewModel();


        // The data access object.
        // TODO: Update the path to the CSV file.
        PlayerDataAccessInterface playerDataAccessObject = new CsvPlayerDataAccessObject("PlayerStatsDataset.csv");
        GeminiDataAccessObject geminiDataAccessObject = new GeminiDataAccessObject();
        TeamDataAccessInterface teamDataAccessObject = new CsvTeamDataAccessObject("TeamStatsDataset.csv");

        UserDataAccessInterface userDataAccessInterface = new FileUserDataAccessObject("src/main/java/data/users.csv");
        AuthPresenter authPresenter = new AuthPresenter(authViewModel, viewManagerModel, mainMenuViewModel);
        LoginInputBoundary loginInputBoundary = new LoginInteractor(userDataAccessInterface, authPresenter);
        SignupInputBoundary signupInputBoundary = new SignupInteractor(userDataAccessInterface, authPresenter);
        AuthController authController = new AuthController(loginInputBoundary, signupInputBoundary);
        AuthView authView = new AuthView(authViewModel, authController);
        views.add(authView, authView.viewName);

        MainMenuOutputBoundary mainMenuPresenter = new MainMenuPresenter(mainMenuViewModel, viewManagerModel, generateInsightsViewModel);
        MainMenuInputBoundary mainMenuInteractor = new MainMenuInteractor(playerDataAccessObject, mainMenuPresenter);
        // Pass the favouriteController to MainMenuController so it can provide favorited players
        MainMenuController mainMenuController = new MainMenuController(mainMenuInteractor, viewManagerModel, favouriteController);
        MainMenuView mainMenuView = new MainMenuView(mainMenuViewModel, mainMenuController);
        views.add(mainMenuView, mainMenuView.viewName);

        GenerateInsightsOutputBoundary generateInsightsOutputBoundary = new GenerateInsightsPresenter(generateInsightsViewModel, viewManagerModel);
        GenerateInsightsInputBoundary generateInsightsInteractor = new GenerateInsightsInteractor(geminiDataAccessObject, generateInsightsOutputBoundary);
        GenerateInsightsController generateInsightsController = new GenerateInsightsController(generateInsightsInteractor, viewManagerModel, mainMenuViewModel);

        GenerateInsightsView generateInsightsView = new GenerateInsightsView(generateInsightsViewModel, generateInsightsController);
        views.add(generateInsightsView, generateInsightsView.viewName);

        // (FavoritedPlayersView will be registered later after SearchPlayerController exists)


        viewManagerModel.setActiveView(authView.viewName);
        viewManagerModel.firePropertyChanged();


        // Set up Compare
        // CompareViewModel compareViewModel = new CompareViewModel();
        // compareViewModel.setViewManagerModel(viewManagerModel);

        // CompareOutputBoundary comparePresenter = new ComparePresenter(compareViewModel);
        // CompareInputBoundary compareInteractor = new CompareInteractor(playerDataAccessObject, teamDataAccessObject, comparePresenter);
        // CompareController compareController = new CompareController(compareInteractor);
        JPanel compareView = CompareFactory.createCompareView(viewManagerModel);

        views.add(compareView, "compare");


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
                new SearchPlayerView(searchPlayerController, searchPlayerViewModel,
                                    favouriteController, favouriteViewModel);

        views.add(searchPlayerView, searchPlayerView.viewName);

        // Now that SearchPlayerController and SearchPlayerView exist, register the favorited players card with them
        FavoritedPlayersView favoritedPlayersView = new FavoritedPlayersView(favouriteViewModel, favouriteController, viewManagerModel, searchPlayerController, searchPlayerView);
        views.add(favoritedPlayersView, favoritedPlayersView.viewName);


        // Sort Players Feature Setup
        SortViewModel sortViewModel = new SortViewModel();
        sortViewModel.setViewManagerModel(viewManagerModel);

        SortOutputBoundary sortPresenter =
                new SortPresenter(sortViewModel, viewManagerModel);

        SortInputBoundary sortInteractor =
                new SortInteractor(sortPresenter);

        SortController sortController =
                new SortController(sortInteractor, sortViewModel);

        SortPlayersView sortPlayersView =
                new SortPlayersView(sortController, sortViewModel);

        views.add(sortPlayersView, sortPlayersView.viewName);


        java.util.Set<String> allTeams = new java.util.HashSet<>();
        java.util.Set<String> allPositions = new java.util.HashSet<>();
        for (var p : playerDataAccessObject.findAll()) {
            if (p.getTeam() != null && p.getTeam().getName() != null && !p.getTeam().getName().isBlank()) {
                allTeams.add(p.getTeam().getName());
            }
            if (p.getPosition() != null && !p.getPosition().isBlank()) {
                allPositions.add(p.getPosition());
            }
        }

        FilterPlayersViewModel filterVM = new FilterPlayersViewModel(allTeams, allPositions);
        FilterPlayersPresenter filterPresenter = new FilterPlayersPresenter(filterVM);
        FilterPlayersInputBoundary filterInteractor =
                new FilterPlayersInteractor(playerDataAccessObject, filterPresenter);
        FilterPlayersController filterController = new FilterPlayersController(filterInteractor);
        FilterPlayersView filterPlayersView = new FilterPlayersView(filterVM, filterController);

        views.add(filterPlayersView, filterPlayersView.viewName);

        AskQuestionPresenter askQuestionPresenter = new AskQuestionPresenter(askQuestionViewModel, viewManagerModel);
        AskQuestionInputBoundary askQuestionInteractor = new AskQuestionInteractor(geminiDataAccessObject, askQuestionPresenter);
        AskQuestionController askQuestionController = new AskQuestionController(askQuestionInteractor);

        ComparePlayersPresenter comparePlayersPresenter = new ComparePlayersPresenter(comparePlayersViewModel, viewManagerModel);
        ComparePlayersInputBoundary comparePlayersInteractor = new ComparePlayersInteractor(geminiDataAccessObject, comparePlayersPresenter);
        ComparePlayersController comparePlayersController = new ComparePlayersController(comparePlayersInteractor);


        ChatView chatView = new ChatView(askQuestionViewModel, askQuestionController, comparePlayersViewModel, comparePlayersController, viewManagerModel);
        views.add(chatView, chatView.viewName);

        application.pack();
        application.setExtendedState(JFrame.MAXIMIZED_BOTH);
        application.setVisible(true);
    }
}