package use_case.favourite;

import java.util.ArrayList;

public class FavouriteOutputData {
    private final boolean success;
    private final ArrayList<String> favourites;
    public FavouriteOutputData(boolean success, ArrayList<String> favourites) {
        this.success = success;
        this.favourites = favourites;
    }

    public boolean isSuccess() {
        return success;
    }

    public ArrayList<String> getFavourites() {
        return favourites;
    }
}
