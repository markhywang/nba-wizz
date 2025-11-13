package interface_adapter.generate_insights;

import interface_adapter.ViewModel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class GenerateInsightsViewModel extends ViewModel {
    public static final String TITLE_LABEL = "Generate AI Insight";
    public static final String GENERATE_BUTTON_LABEL = "Generate";
    public static final String BACK_BUTTON_LABEL = "Back";

    private GenerateInsightsState state = new GenerateInsightsState();

    public GenerateInsightsViewModel() {
        super("generate_insights");
    }

    public void setState(GenerateInsightsState state) {
        this.state = state;
    }

    public GenerateInsightsState getState() {
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
