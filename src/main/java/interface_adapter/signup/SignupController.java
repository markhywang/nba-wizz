package interface_adapter.signup;

import use_case.authentication.signup.SignupInputBoundary;
import use_case.authentication.signup.SignupInputData;

public class SignupController {

    final SignupInputBoundary userSignupUseCaseInteractor;
    public SignupController(SignupInputBoundary userSignupUseCaseInteractor) {
        this.userSignupUseCaseInteractor = userSignupUseCaseInteractor;
    }

    public void execute(String username, String password, String repeatPassword) {
        SignupInputData signupInputData = new SignupInputData(
                username, password, repeatPassword);

        userSignupUseCaseInteractor.execute(signupInputData);
    }

    public void switchToLogin() {
        userSignupUseCaseInteractor.switchToLogin();
    }
}
