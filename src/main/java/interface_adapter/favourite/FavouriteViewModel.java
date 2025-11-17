package interface_adapter.favourite;

import interface_adapter.ViewModel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class FavouriteViewModel extends ViewModel {

    private FavouriteState state = new FavouriteState();

    public FavouriteViewModel() {
        super("favourites");
    }

    public void setState(FavouriteState state) {
        this.state = state;
    }

    public FavouriteState getState() {
        return state;
    }

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    @Override
    public void firePropertyChanged() {
        support.firePropertyChange("favouriteState", null, this.state);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}
