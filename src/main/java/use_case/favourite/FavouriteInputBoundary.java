package use_case.favourite;

import java.util.ArrayList;

public interface FavouriteInputBoundary {
    void toggle(FavouriteInputData favouriteInputData);
    ArrayList<String> getFavourites();
    boolean isFavourite(FavouriteInputData favouriteInputData);
}
