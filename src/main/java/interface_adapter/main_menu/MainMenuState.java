package interface_adapter.main_menu;

public class MainMenuState {
    private String error = null;
    private String activeView;

    public MainMenuState(MainMenuState copy) {
        error = copy.error;
        activeView = copy.activeView;
    }

    public MainMenuState() {}

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getActiveView() {
        return activeView;
    }

    public void setActiveView(String activeView) {
        this.activeView = activeView;
    }
}
