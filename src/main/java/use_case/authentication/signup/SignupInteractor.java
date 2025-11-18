package use_case.authentication.signup;

import entity.User;
import use_case.authentication.PasswordHasher;
import use_case.authentication.UserDataAccessInterface;

public class SignupInteractor implements SignupInputBoundary {

    private final UserDataAccessInterface userDataAccessInterface;
    private final SignupOutputBoundary signupOutputBoundary;

    public SignupInteractor(UserDataAccessInterface userDataAccessInterface,
                            SignupOutputBoundary signupOutputBoundary) {
        this.userDataAccessInterface = userDataAccessInterface;
        this.signupOutputBoundary = signupOutputBoundary;
    }

    @Override
    public void execute(SignupInputData inputData) {
        String username = inputData.getUsername();
        String password = inputData.getPassword();

        if (username == null || username.trim().isEmpty()) {
            signupOutputBoundary.prepareSignupFail("Username is required.");
            return;
        }

        if (password == null || password.length() < 4) {
            signupOutputBoundary.prepareSignupFail("Password must be at least 4 characters.");
            return;
        }

        if (userDataAccessInterface.existsByUsername(username)) {
            signupOutputBoundary.prepareSignupFail("Username is already taken.");
            return;
        }

        String passwordHash = PasswordHasher.hash(password);
        User user = new User(username.trim(), passwordHash);
        userDataAccessInterface.save(user);

        signupOutputBoundary.prepareSuccessView(new SignupOutputData(user.getUsername()));
    }
}


