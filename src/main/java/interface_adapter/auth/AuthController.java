package interface_adapter.auth;

import use_case.authentication.login.LoginInputBoundary;
import use_case.authentication.login.LoginInputData;
import use_case.authentication.signup.SignupInputBoundary;
import use_case.authentication.signup.SignupInputData;

public class AuthController {
    private final LoginInputBoundary loginInputBoundary;
    private final SignupInputBoundary signupInputBoundary;

    public AuthController(LoginInputBoundary loginInputBoundary,
                          SignupInputBoundary signupInputBoundary) {
        this.loginInputBoundary = loginInputBoundary;
        this.signupInputBoundary = signupInputBoundary;
    }

    public void login(String username, String password) {
        loginInputBoundary.execute(new LoginInputData(username, password));
    }

    public void signup(String username, String password) {
        signupInputBoundary.execute(new SignupInputData(username, password));
    }

}


