package use_case.favourite;

public interface FavouriteOutputBoundary {
    void addFavourite(FavouriteOutputData favouriteOutputData);
    void removeFavourite(FavouriteOutputData favouriteOutputData);
}
