package use_case.compare_players;

import entity.Answer;
import entity.Player;

import java.util.Optional;

public class ComparePlayersInteractor implements ComparePlayersInputBoundary {
    private final ComparePlayersDataAccessInterface dataAccess;
    private final ComparePlayersOutputBoundary presenter;

    public ComparePlayersInteractor(ComparePlayersDataAccessInterface dataAccess, ComparePlayersOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(ComparePlayersInputData inputData) {
        if (inputData.player1Name() == null || inputData.player1Name().trim().isEmpty() ||
            inputData.player2Name() == null || inputData.player2Name().trim().isEmpty()) {
            presenter.prepareFailView("Player names cannot be empty.");
            return;
        }

        presenter.prepareLoadingView();

        new Thread(() -> {
            Optional<Player> player1Optional = dataAccess.getPlayerByName(inputData.player1Name());
            Optional<Player> player2Optional = dataAccess.getPlayerByName(inputData.player2Name());

            if (player1Optional.isEmpty()) {
                presenter.prepareFailView("Player 1 not found.");
                return;
            }

            if (player2Optional.isEmpty()) {
                presenter.prepareFailView("Player 2 not found.");
                return;
            }

            try {
                String comparisonText = dataAccess.getPlayerComparison(player1Optional.get(), player2Optional.get());
                Answer comparison = new Answer(comparisonText);
                ComparePlayersOutputData outputData = new ComparePlayersOutputData(comparison, false);
                presenter.prepareSuccessView(outputData);
            } catch (Exception e) {
                presenter.prepareFailView("An unexpected error occurred: " + e.getMessage());
            }
        }).start();
    }
}
