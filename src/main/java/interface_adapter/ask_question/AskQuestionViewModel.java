package interface_adapter.ask_question;

import interface_adapter.ViewModel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class AskQuestionViewModel extends ViewModel {

    private AskQuestionState state = new AskQuestionState();

    public AskQuestionViewModel() {
        super("ask_question");
    }

    public void setState(AskQuestionState state) {
        this.state = state;
    }

    public AskQuestionState getState() {
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
