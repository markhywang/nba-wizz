package use_case.authentication.login;

public interface LoginOutputBoundary {
    void prepareSuccessView(LoginOutputData data);
    void prepareLoginFail(String error);
    void prepareSignupView();
}


