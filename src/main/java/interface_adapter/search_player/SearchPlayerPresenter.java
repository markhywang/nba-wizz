package interface_adapter.search_player;

import entity.Player;
import use_case.search_player.SearchPlayerOutputBoundary;
import use_case.search_player.SearchPlayerOutputData;

public class SearchPlayerPresenter implements SearchPlayerOutputBoundary {
    private final SearchPlayerViewModel viewModel;

    public SearchPlayerPresenter(SearchPlayerViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(SearchPlayerOutputData outputData) {
        SearchPlayerState state = viewModel.getState();
        Player player = outputData.getPlayer();

        state.setPlayerName(player.getName());
        state.setPlayerTeam(player.getTeam().getName());
        state.setPlayerPosition(player.getPosition());
        state.setMessage("");

        viewModel.firePropertyChanged();

    }

    @Override
    public void presentPlayerNotFound(String message) {
        SearchPlayerState state = viewModel.getState();

        state.setPlayerName("");
        state.setPlayerTeam("");
        state.setPlayerPosition("");
        state.setMessage(message);

        viewModel.firePropertyChanged();
    }
}
