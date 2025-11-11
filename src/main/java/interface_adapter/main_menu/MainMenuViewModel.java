package interface_adapter.main_menu;

import interface_adapter.ViewModel;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class MainMenuViewModel extends ViewModel {
    public static final String TITLE_LABEL = "NBA WIZZ";
    public static final String SEARCH_FOR_PLAYER_BUTTON_LABEL = "Search for player";
    public static final String FILTER_SORT_BUTTON_LABEL = "Filter & Sort";
    public static final String COMPARE_BUTTON_LABEL = "Compare";
    public static final String AI_INSIGHTS_BUTTON_LABEL = "AI Insights";

    private MainMenuState state = new MainMenuState();
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public MainMenuViewModel() {super("main_menu");
    }

    public void setState(MainMenuState state) {
        this.state = state;
    }

    public MainMenuState getState() {
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
