package interface_adapter.main_menu;

public class MainMenuState {
    private String error = null;

    public MainMenuState(MainMenuState copy) {
        error = copy.error;
    }

    public MainMenuState() {}

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
