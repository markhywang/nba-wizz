package interface_adapter.login;

import interface_adapter.ViewManagerModel;
import interface_adapter.main_menu.MainMenuViewModel;
import interface_adapter.signup.SignupViewModel;
import use_case.authentication.login.LoginOutputBoundary;
import use_case.authentication.login.LoginOutputData;
import use_case.favourite.FavouriteDataAccessInterface;
import use_case.favourite.FavouriteOutputBoundary;
import use_case.favourite.FavouriteOutputData;

public class LoginPresenter implements LoginOutputBoundary {

    private final LoginViewModel loginViewModel;
    private final SignupViewModel signupViewModel;
    private final MainMenuViewModel mainMenuViewModel;
    private final ViewManagerModel viewManagerModel;

    private final FavouriteDataAccessInterface favouriteDataAccessInterface;
    private final FavouriteOutputBoundary favouriteOutputBoundary;

    public LoginPresenter(ViewManagerModel viewManagerModel,
                          MainMenuViewModel mainMenuViewModel,
                          LoginViewModel loginViewModel,
                          SignupViewModel signupViewModel,
                          FavouriteDataAccessInterface favouriteDataAccessInterface,
                          FavouriteOutputBoundary favouriteOutputBoundary) {
        this.viewManagerModel = viewManagerModel;
        this.mainMenuViewModel = mainMenuViewModel;
        this.loginViewModel = loginViewModel;
        this.signupViewModel = signupViewModel;
        this.favouriteDataAccessInterface = favouriteDataAccessInterface;
        this.favouriteOutputBoundary = favouriteOutputBoundary;
    }

    @Override
    public void prepareSuccessView(LoginOutputData response) {
        // On success, switch to the logged in view.
        // Optionally update the state with success info or clear fields
        LoginState loginState = loginViewModel.getState();
        loginState.setUsername("");
        loginState.setPassword("");
        loginState.setError(null);
        loginViewModel.setState(loginState);
        loginViewModel.firePropertyChanged();

        this.viewManagerModel.setActiveView(mainMenuViewModel.getViewName());
        this.viewManagerModel.firePropertyChanged();
        // Set the active user for per-user favourites and refresh the favourites view model
        if (favouriteDataAccessInterface != null) {
            favouriteDataAccessInterface.setCurrentUser(response.getUsername());
            if (favouriteOutputBoundary != null) {
                favouriteOutputBoundary.addFavourite(new FavouriteOutputData(true, favouriteDataAccessInterface.getFavourites()));
            }
        }
    }

    @Override
    public void prepareLoginFail(String error) {
        LoginState loginState = loginViewModel.getState();
        loginState.setError(error);
        loginViewModel.firePropertyChanged();
    }

    @Override
    public void prepareSignupView() {
        // Switch to the Signup View
        this.viewManagerModel.setActiveView(signupViewModel.getViewName());
        this.viewManagerModel.firePropertyChanged();
    }
}
