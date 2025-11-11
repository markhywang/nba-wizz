package use_case.search_player;

import data_access.PlayerDataAccessInterface;
import entity.Player;

public class SearchPlayerInteractor implements SearchPlayerInputBoundary {
    private final PlayerDataAccessInterface playerDataAccessObject;
    private final SearchPlayerOutputBoundary presenter;

    public SearchPlayerInteractor(PlayerDataAccessInterface playerDataAccessObject, SearchPlayerOutputBoundary
            presenter) {
        this.playerDataAccessObject = playerDataAccessObject;
        this.presenter = presenter;
    }

    @Override
    public void execute(SearchPlayerInputData inputData) {
        Player player = playerDataAccessObject.getPlayerByName(inputData.getPlayerName());
        if (player == null) {
            presenter.presentPlayerNotFound("Player not found");
        }
        else {
            presenter.present(new SearchPlayerOutputData(player));
        }
    }
}


