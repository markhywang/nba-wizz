package interface_adapter.search_player;

import use_case.search_player.SearchPlayerInputBoundary;
import use_case.search_player.SearchPlayerInputData;

public class SearchPlayerController {
    private final SearchPlayerInputBoundary interactor;

    public SearchPlayerController(SearchPlayerInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void onSearch(String searchText) {
        interactor.execute(new SearchPlayerInputData(searchText));
    }
}
