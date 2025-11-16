package interface_adapter.compare;

import interface_adapter.ViewManagerModel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class CompareViewModel {

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private CompareState state = new CompareState();
    private ViewManagerModel viewManagerModel;

    public CompareState getState() {
        return state;
    }

    public void setState(CompareState newState) {
        this.state = newState;
        propertyChangeSupport.firePropertyChange("state", null, this.state);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void setViewManagerModel(ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;
    }

    public ViewManagerModel getViewManagerModel() {
        return viewManagerModel;
    }
}
