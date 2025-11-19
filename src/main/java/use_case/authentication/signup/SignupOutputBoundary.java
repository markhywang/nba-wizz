package use_case.authentication.signup;

public interface SignupOutputBoundary {
    void prepareSuccessView(SignupOutputData data);
    void prepareSignupFail(String error);
    void prepareLoginView();
}


