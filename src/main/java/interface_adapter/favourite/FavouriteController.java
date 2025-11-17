package interface_adapter.favourite;

import use_case.favourite.FavouriteInputBoundary;
import use_case.favourite.FavouriteInputData;

public class FavouriteController {
    private final FavouriteInputBoundary favouriteInputBoundary;

    public FavouriteController(FavouriteInputBoundary favouriteInputBoundary) {
        this.favouriteInputBoundary = favouriteInputBoundary;
    }

    public void favouriteToggle(String playerName) {
        this.favouriteInputBoundary.toggle(new FavouriteInputData(playerName.toLowerCase()));
    }

    public boolean isFavourite(String playerName) {
        return this.favouriteInputBoundary.isFavourite(new FavouriteInputData(playerName));
    }
}
