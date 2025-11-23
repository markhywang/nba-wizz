package view;

import interface_adapter.login.LoginController;
import interface_adapter.login.LoginState;
import interface_adapter.login.LoginViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class LoginView extends JPanel implements ActionListener, PropertyChangeListener {

    public final String viewName = "log in";
    private final LoginViewModel loginViewModel;

    private final JTextField usernameInputField = new JTextField(15);
    private final JPasswordField passwordInputField = new JPasswordField(15);
    private final JLabel errorLabel = new JLabel();
    private final JLabel infoLabel = new JLabel();

    private final JButton logIn;
    private final JButton signUp;
    private final LoginController loginController;

    public LoginView(LoginViewModel loginViewModel, LoginController controller) {

        this.loginController = controller;
        this.loginViewModel = loginViewModel;
        this.loginViewModel.addPropertyChangeListener(this);

        // Styling
        Font titleFont = new Font("Arial", Font.BOLD, 24);
        Font fieldFont = new Font("Arial", Font.PLAIN, 18);
        Font buttonFont = new Font("Arial", Font.BOLD, 16);

        JLabel title = new JLabel(LoginViewModel.TITLE_LABEL);
        title.setFont(titleFont);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Setup fields
        usernameInputField.setFont(fieldFont);
        usernameInputField.setColumns(20);
        passwordInputField.setFont(fieldFont);
        passwordInputField.setColumns(20);

        JLabel usernameLabel = new JLabel(LoginViewModel.USERNAME_LABEL);
        usernameLabel.setFont(fieldFont);
        JLabel passwordLabel = new JLabel(LoginViewModel.PASSWORD_LABEL);
        passwordLabel.setFont(fieldFont);

        LabelTextPanel usernameInfo = new LabelTextPanel(usernameLabel, usernameInputField);
        LabelTextPanel passwordInfo = new LabelTextPanel(passwordLabel, passwordInputField);

        JPanel buttons = new JPanel();
        logIn = new JButton(LoginViewModel.LOGIN_BUTTON_LABEL);
        logIn.setFont(buttonFont);
        logIn.setPreferredSize(new Dimension(120, 40));
        buttons.add(logIn);
        
        signUp = new JButton(LoginViewModel.SIGNUP_BUTTON_LABEL);
        signUp.setFont(buttonFont);
        signUp.setPreferredSize(new Dimension(120, 40));
        buttons.add(signUp);

        logIn.addActionListener(this);
        signUp.addActionListener(this);

        // Layout
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Arial", Font.BOLD, 14));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        infoLabel.setForeground(Color.GREEN);
        infoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.add(title, gbc);
        
        gbc.gridy++;
        this.add(usernameInfo, gbc);
        
        gbc.gridy++;
        this.add(passwordInfo, gbc);
        
        gbc.gridy++;
        this.add(errorLabel, gbc);
        
        gbc.gridy++;
        this.add(infoLabel, gbc);
        
        gbc.gridy++;
        this.add(buttons, gbc);
    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource().equals(logIn)) {
            LoginState currentState = loginViewModel.getState();
            
            String username = usernameInputField.getText();
            String password = new String(passwordInputField.getPassword());
            
            currentState.setUsername(username);
            currentState.setPassword(password);
            loginViewModel.setState(currentState);

            loginController.execute(username, password);
        } else if (evt.getSource().equals(signUp)) {
            loginController.switchToSignup();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        LoginState state = (LoginState) evt.getNewValue();
        if (state.getError() != null) {
            errorLabel.setText(state.getError());
        } else {
            errorLabel.setText("");
        }
        
        if (state.getInfoMessage() != null) {
            infoLabel.setText(state.getInfoMessage());
        } else {
            infoLabel.setText("");
        }
        
        // Update fields if state changes (e.g. cleared on logout)
        // Avoid resetting if user is typing? 
        // Typically we only update text fields if the state value is different from what's in the box
        // or if we want to force clear.
        // For now, let's just handle error/info.
    }
}
