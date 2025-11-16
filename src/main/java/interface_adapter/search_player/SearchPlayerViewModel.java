package interface_adapter.search_player;

import interface_adapter.ViewManagerModel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class SearchPlayerViewModel {
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private SearchPlayerState state = new SearchPlayerState();
    private ViewManagerModel viewManagerModel = new ViewManagerModel();

    public SearchPlayerState getState() {
        return state;
    }

    public void setState(SearchPlayerState newState) {
        this.state = newState;
        firePropertyChanged();
    }

    public void setViewManagerModel(ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;
    }

    public ViewManagerModel getViewManagerModel() {
        return viewManagerModel;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void firePropertyChanged() {
        support.firePropertyChange("state", null, this.state);
    }
}