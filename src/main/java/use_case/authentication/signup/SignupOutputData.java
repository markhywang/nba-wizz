package use_case.authentication.signup;

public class SignupOutputData {
    private final String username;

    public SignupOutputData(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}


