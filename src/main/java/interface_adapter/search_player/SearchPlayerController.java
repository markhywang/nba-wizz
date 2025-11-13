package interface_adapter.search_player;

import use_case.search_player.SearchPlayerInputBoundary;
import use_case.search_player.SearchPlayerInputData;

import java.util.List;

public class SearchPlayerController {
    private final SearchPlayerInputBoundary interactor;

    public SearchPlayerController(SearchPlayerInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void executeSearch(String playerName, String startSeason, String endSeason, List<String> statSelections) {
        SearchPlayerInputData inputData = new SearchPlayerInputData(
                playerName, startSeason, endSeason, statSelections
        );

        interactor.execute(inputData);
    }
}
