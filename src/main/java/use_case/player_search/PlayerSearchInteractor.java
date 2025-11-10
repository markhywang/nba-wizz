package use_case.player_search;

import data_access.PlayerDataAccessInterface;
import entity.Player;

import java.util.Optional;

public class PlayerSearchInteractor implements PlayerSearchInputBoundary {
    private final PlayerDataAccessInterface playerDataAccessObject;
    private final PlayerSearchOutputBoundary playerSearchPresenter;

    public PlayerSearchInteractor(PlayerDataAccessInterface playerDataAccessObject, PlayerSearchOutputBoundary playerSearchPresenter) {
        this.playerDataAccessObject = playerDataAccessObject;
        this.playerSearchPresenter = playerSearchPresenter;
    }

    @Override
    public void execute(PlayerSearchInputData playerSearchInputData) {
        // For simplicity, this interactor will find a player by name.
        // A real implementation would be more complex.
        Optional<Player> playerOptional = playerDataAccessObject.findAll().stream()
                .filter(p -> p.getName().equalsIgnoreCase(playerSearchInputData.getPlayerName()))
                .findFirst();

        if (playerOptional.isPresent()) {
            Player player = playerOptional.get();
            PlayerSearchOutputData outputData = new PlayerSearchOutputData(player, false);
            playerSearchPresenter.present(outputData);
        } else {
            // Handle player not found
            playerSearchPresenter.present(new PlayerSearchOutputData(null, true));
        }
    }
}
