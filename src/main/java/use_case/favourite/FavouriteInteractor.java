package use_case.favourite;

import java.util.ArrayList;

public class FavouriteInteractor implements FavouriteInputBoundary{
    private final FavouriteOutputBoundary favouriteOutputBoundary;
    private final FavouriteDataAccessInterface favouriteDataAccessInterface;

    public FavouriteInteractor(FavouriteOutputBoundary favouriteOutputBoundary, FavouriteDataAccessInterface favouriteDataAccessInterface) {
        this.favouriteOutputBoundary = favouriteOutputBoundary;
        this.favouriteDataAccessInterface = favouriteDataAccessInterface;
    }

    public void toggle(FavouriteInputData favouriteInputData) {
        if (isFavourite(favouriteInputData)) {
            this.remove(favouriteInputData);
        }
        else {
            this.add(favouriteInputData);
        }
    }

    public void add(FavouriteInputData favouriteInputData) {
        favouriteDataAccessInterface.add(favouriteInputData.playerName);
        favouriteDataAccessInterface.save();
        favouriteOutputBoundary.addFavourite(new FavouriteOutputData(true, favouriteDataAccessInterface.getFavourites()));
    }

    @Override
    public boolean isFavourite(FavouriteInputData favouriteInputData) {
        return this.favouriteDataAccessInterface.isFavourite(favouriteInputData.playerName);
    }


    public void remove(FavouriteInputData favouriteInputData) {
        favouriteDataAccessInterface.remove(favouriteInputData.playerName);
        favouriteDataAccessInterface.save();
        favouriteOutputBoundary.removeFavourite(new FavouriteOutputData(true, favouriteDataAccessInterface.getFavourites()));
    }

    @Override
    public ArrayList<String> getFavourites() {
        return favouriteDataAccessInterface.getFavourites();
    }
}
