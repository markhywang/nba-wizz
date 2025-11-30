package interface_adapter.compare_players;

import interface_adapter.ViewModel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ComparePlayersViewModel extends ViewModel {

    private ComparePlayersState state = new ComparePlayersState();

    public ComparePlayersViewModel() {
        super("compare_players");
    }

    public void setState(ComparePlayersState state) {
        this.state = state;
    }

    public ComparePlayersState getState() {
        return state;
    }

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    @Override
    public void firePropertyChanged() {
        support.firePropertyChange("state", null, this.state);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}
