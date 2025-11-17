package use_case.favourite;

import java.util.ArrayList;

public interface FavouriteDataAccessInterface {
    void add(String playerName);
    void remove(String playerName);
    boolean isFavourite(String playerName);
    ArrayList<String> getFavourites();
    void save();
}
