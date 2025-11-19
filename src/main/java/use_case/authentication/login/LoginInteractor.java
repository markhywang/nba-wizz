package use_case.authentication.login;

import entity.User;
import use_case.authentication.PasswordHasher;
import use_case.authentication.UserDataAccessInterface;

import java.util.Optional;

public class LoginInteractor implements LoginInputBoundary {

    private final UserDataAccessInterface userDataAccessInterface;
    private final LoginOutputBoundary loginOutputBoundary;

    public LoginInteractor(UserDataAccessInterface userDataAccessInterface,
                           LoginOutputBoundary loginOutputBoundary) {
        this.userDataAccessInterface = userDataAccessInterface;
        this.loginOutputBoundary = loginOutputBoundary;
    }

    @Override
    public void execute(LoginInputData inputData) {
        String username = inputData.getUsername();
        String password = inputData.getPassword();

        if (username == null || username.trim().isEmpty()) {
            loginOutputBoundary.prepareLoginFail("Username is required.");
            return;
        }

        if (password == null || password.isEmpty()) {
            loginOutputBoundary.prepareLoginFail("Password is required.");
            return;
        }

        Optional<User> userOptional = userDataAccessInterface.findByUsername(username);
        if (userOptional.isEmpty()) {
            loginOutputBoundary.prepareLoginFail("No account found for that username.");
            return;
        }

        String passwordHash = PasswordHasher.hash(password);
        User user = userOptional.get();
        if (!user.getPasswordHash().equals(passwordHash)) {
            loginOutputBoundary.prepareLoginFail("Invalid username or password.");
            return;
        }

        loginOutputBoundary.prepareSuccessView(new LoginOutputData(user.getUsername()));
    }

    @Override
    public void switchToSignup() {
        loginOutputBoundary.prepareSignupView();
    }
}


