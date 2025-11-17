package interface_adapter.auth;

import interface_adapter.ViewModel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class AuthViewModel extends ViewModel {
    public static final String TITLE = "Sign in to NBA Wizz";
    private AuthState state = new AuthState();
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public AuthViewModel() {
        super("auth");
    }

    public AuthState getState() {
        return state;
    }

    public void setState(AuthState state) {
        this.state = state;
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


