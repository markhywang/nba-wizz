package interface_adapter.signup;

import interface_adapter.ViewManagerModel;
import interface_adapter.login.LoginState;
import interface_adapter.login.LoginViewModel;
import use_case.authentication.signup.SignupOutputBoundary;
import use_case.authentication.signup.SignupOutputData;

public class SignupPresenter implements SignupOutputBoundary {

    private final SignupViewModel signupViewModel;
    private final LoginViewModel loginViewModel;
    private final ViewManagerModel viewManagerModel;

    public SignupPresenter(ViewManagerModel viewManagerModel,
                           SignupViewModel signupViewModel,
                           LoginViewModel loginViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.signupViewModel = signupViewModel;
        this.loginViewModel = loginViewModel;
    }

    @Override
    public void prepareSuccessView(SignupOutputData response) {
        // On success, switch to the login view.
        // Clear signup state
        SignupState signupState = signupViewModel.getState();
        signupState.setUsername("");
        signupState.setPassword("");
        signupState.setRepeatPassword("");
        signupState.setError(null);
        signupViewModel.setState(signupState);
        signupViewModel.firePropertyChanged();

        // Update login state with info message and username
        LoginState loginState = loginViewModel.getState();
        loginState.setUsername(response.username());
        loginState.setInfoMessage("Account created successfully. Please log in.");
        loginState.setError(null);
        loginViewModel.setState(loginState);
        loginViewModel.firePropertyChanged();

        this.viewManagerModel.setActiveView(loginViewModel.getViewName());
        this.viewManagerModel.firePropertyChanged();
    }

    @Override
    public void prepareSignupFail(String error) {
        SignupState signupState = signupViewModel.getState();
        signupState.setError(error);
        signupViewModel.firePropertyChanged();
    }

    @Override
    public void prepareLoginView() {
        this.viewManagerModel.setActiveView(loginViewModel.getViewName());
        this.viewManagerModel.firePropertyChanged();
    }
}
