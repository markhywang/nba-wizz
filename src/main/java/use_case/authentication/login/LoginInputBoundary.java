package use_case.authentication.login;

public interface LoginInputBoundary {
    void execute(LoginInputData inputData);
    void switchToSignup();
}


