package interface_adapter.filter_players;

import interface_adapter.ViewModel;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Set;

/** ViewModel for the Filter Players screen. */
public class FilterPlayersViewModel extends ViewModel {
    private final Set<String> allTeams;
    private final Set<String> allPositions;

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    private FilterPlayersState state = new FilterPlayersState();

    public FilterPlayersViewModel(Set<String> allTeams, Set<String> allPositions) {
        super("filter players");
        this.allTeams = allTeams;
        this.allPositions = allPositions;
    }

    public Set<String> getAllTeams() { return allTeams; }
    public Set<String> getAllPositions() { return allPositions; }

    public FilterPlayersState getState() { return state; }
    public void setState(FilterPlayersState state) {
        this.state = state;
        firePropertyChanged();
    }


    @Override
    public void firePropertyChanged() {
        support.firePropertyChange("state", null, this.state);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}
