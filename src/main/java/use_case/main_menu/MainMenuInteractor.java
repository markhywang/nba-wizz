package use_case.main_menu;

import data_access.PlayerDataAccessInterface;
import entity.Player;

import java.util.Optional;

public class MainMenuInteractor implements MainMenuInputBoundary {
    private final PlayerDataAccessInterface playerDataAccessObject;
    private final MainMenuOutputBoundary mainMenuPresenter;

    public MainMenuInteractor(PlayerDataAccessInterface playerDataAccessObject, MainMenuOutputBoundary mainMenuPresenter) {
        this.playerDataAccessObject = playerDataAccessObject;
        this.mainMenuPresenter = mainMenuPresenter;
    }

    @Override
    public void execute(MainMenuInputData mainMenuInputData) {
        // For simplicity, this interactor will find a player by name.
        // A real implementation would be more complex.
        Optional<Player> playerOptional = playerDataAccessObject.findAll().stream()
                .filter(p -> p.getName().equalsIgnoreCase(mainMenuInputData.getPlayerName()))
                .findFirst();

        if (playerOptional.isPresent()) {
            Player player = playerOptional.get();
            MainMenuOutputData outputData = new MainMenuOutputData(player, false);
            mainMenuPresenter.present(outputData);
        } else {
            // Handle player not found
            mainMenuPresenter.present(new MainMenuOutputData(null, true));
        }
    }
}
