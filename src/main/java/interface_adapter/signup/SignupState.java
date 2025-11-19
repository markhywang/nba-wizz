package interface_adapter.signup;

public class SignupState {
    private String username = "";
    private String password = "";
    private String repeatPassword = "";
    private String error = null;

    public SignupState(SignupState copy) {
        username = copy.username;
        password = copy.password;
        repeatPassword = copy.repeatPassword;
        error = copy.error;
    }

    // Because of the previous copy constructor, the default constructor must be explicit.
    public SignupState() {}

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public String getError() {
        return error;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

    public void setError(String error) {
        this.error = error;
    }
}
