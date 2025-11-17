package interface_adapter.favourite;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class FavouriteState {
    ArrayList<String> favourites;
    public FavouriteState() {
        this.favourites = new ArrayList<>();
    }
    public FavouriteState(ArrayList<String> favourites) {
        this.favourites = favourites;
    }
    public boolean isFavourite(String playerName) {
        return favourites.contains(playerName);
    }
    public ArrayList<String> getFavourites() {
        return this.favourites;
    }
}
