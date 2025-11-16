package app;

import data_access.CsvPlayerDataAccessObject;
import data_access.CsvTeamDataAccessObject;
import data_access.PlayerDataAccessInterface;
import data_access.TeamDataAccessInterface;
import interface_adapter.ViewManagerModel;
import interface_adapter.compare.CompareController;
import interface_adapter.compare.ComparePresenter;
import interface_adapter.compare.CompareViewModel;
import use_case.compare.CompareInputBoundary;
import use_case.compare.CompareInteractor;
import use_case.compare.CompareOutputBoundary;
import view.compare.CompareView;

import javax.swing.*;

public class CompareFactory {

    /**
     * Builds the full Compare stack and returns the Swing panel.
     * This does not change any provided code – it is pure “student code”.
     */
    public static JPanel createCompareView(ViewManagerModel viewManagerModel) {
        // Data access (you can tweak file names if needed)
        PlayerDataAccessInterface playerDAO =
                new CsvPlayerDataAccessObject("PlayerStatsDataset.csv");
        TeamDataAccessInterface teamDAO =
                new CsvTeamDataAccessObject("PlayerStatsDataset.csv");

        // ViewModel
        CompareViewModel viewModel = new CompareViewModel();

        // Presenter & Interactor
        CompareOutputBoundary presenter = new ComparePresenter(viewModel);
        CompareInputBoundary interactor =
                new CompareInteractor(playerDAO, teamDAO, presenter);

        // Controller
        CompareController controller = new CompareController(interactor);

        // Swing panel (view)
        return new CompareView(controller, viewModel);
    }
}
