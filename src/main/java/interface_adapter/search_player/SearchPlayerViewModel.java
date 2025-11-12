package interface_adapter.search_player;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class SearchPlayerViewModel {
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private SearchPlayerState state = new SearchPlayerState();

    public SearchPlayerState getState() {
        return state;
    }

    public void setState(SearchPlayerState newState) {
        this.state = newState;
        firePropertyChanged();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public void firePropertyChanged() {
        support.firePropertyChange("state", null, this.state);
    }
}