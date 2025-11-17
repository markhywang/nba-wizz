package interface_adapter.sort_players;

import interface_adapter.ViewManagerModel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


public class SortViewModel {

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    private SortState state = new SortState();

    private ViewManagerModel viewManagerModel = new ViewManagerModel();

    public SortState getState() {
        return state;
    }

    public void setState(SortState state) {
        this.state = state;
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
