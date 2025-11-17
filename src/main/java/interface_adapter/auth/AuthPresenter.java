package interface_adapter.auth;

import interface_adapter.ViewManagerModel;
import interface_adapter.main_menu.MainMenuViewModel;
import use_case.authentication.login.LoginOutputBoundary;
import use_case.authentication.login.LoginOutputData;
import use_case.authentication.signup.SignupOutputBoundary;
import use_case.authentication.signup.SignupOutputData;

public class AuthPresenter implements LoginOutputBoundary, SignupOutputBoundary {

    private final AuthViewModel authViewModel;
    private final ViewManagerModel viewManagerModel;
    private final MainMenuViewModel mainMenuViewModel;

    public AuthPresenter(AuthViewModel authViewModel,
                         ViewManagerModel viewManagerModel,
                         MainMenuViewModel mainMenuViewModel) {
        this.authViewModel = authViewModel;
        this.viewManagerModel = viewManagerModel;
        this.mainMenuViewModel = mainMenuViewModel;
    }

    @Override
    public void prepareSuccessView(LoginOutputData data) {
        AuthState state = authViewModel.getState();
        state.setError(null);
        state.setInfoMessage("Welcome back, " + data.getUsername() + "!");
        authViewModel.setState(state);
        authViewModel.firePropertyChanged();

        viewManagerModel.setActiveView(mainMenuViewModel.getViewName());
        viewManagerModel.firePropertyChanged();
    }

    @Override
    public void prepareLoginFail(String error) {
        AuthState state = authViewModel.getState();
        state.setInfoMessage(null);
        state.setError(error);
        authViewModel.setState(state);
        authViewModel.firePropertyChanged();
    }

    @Override
    public void prepareSuccessView(SignupOutputData data) {
        AuthState state = authViewModel.getState();
        state.setError(null);
        state.setInfoMessage("Account created for " + data.getUsername() + ". Please log in.");
        authViewModel.setState(state);
        authViewModel.firePropertyChanged();
    }

    @Override
    public void prepareSignupFail(String error) {
        AuthState state = authViewModel.getState();
        state.setInfoMessage(null);
        state.setError(error);
        authViewModel.setState(state);
        authViewModel.firePropertyChanged();
    }
}


