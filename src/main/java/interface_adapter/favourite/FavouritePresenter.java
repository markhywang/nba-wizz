package interface_adapter.favourite;

import use_case.favourite.FavouriteOutputBoundary;
import use_case.favourite.FavouriteOutputData;

public class FavouritePresenter implements FavouriteOutputBoundary {
    private final FavouriteViewModel favouriteViewModel;
    public FavouritePresenter(FavouriteViewModel favouriteViewModel) {
        this.favouriteViewModel = favouriteViewModel;
    }

    @Override
    public void addFavourite(FavouriteOutputData favouriteOutputData) {
        FavouriteState favouriteState = new FavouriteState(favouriteOutputData.getFavourites());
        this.favouriteViewModel.setState(favouriteState);
        this.favouriteViewModel.firePropertyChanged();
    }

    @Override
    public void removeFavourite(FavouriteOutputData favouriteOutputData) {
        FavouriteState favouriteState = new FavouriteState(favouriteOutputData.getFavourites());
        this.favouriteViewModel.setState(favouriteState);
        this.favouriteViewModel.firePropertyChanged();
    }
}
