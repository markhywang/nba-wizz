package use_case.favourite;

import java.util.ArrayList;

public record FavouriteOutputData(boolean success, ArrayList<String> favourites) {
}
