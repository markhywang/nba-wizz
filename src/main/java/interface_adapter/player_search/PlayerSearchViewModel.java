package interface_adapter.player_search;

import interface_adapter.ViewModel;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class PlayerSearchViewModel extends ViewModel {
    public static final String TITLE_LABEL = "NBA WIZZ";
    public static final String SEARCH_FOR_PLAYER_BUTTON_LABEL = "Search for player";
    public static final String FILTER_SORT_BUTTON_LABEL = "Filter & Sort";
    public static final String COMPARE_BUTTON_LABEL = "Compare";
    public static final String AI_INSIGHTS_BUTTON_LABEL = "AI Insights";

    private PlayerSearchState state = new PlayerSearchState();
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public PlayerSearchViewModel() {
        super("player_search");
    }

    public void setState(PlayerSearchState state) {
        this.state = state;
    }

    public PlayerSearchState getState() {
        return state;
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
