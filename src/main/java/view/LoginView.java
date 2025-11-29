package view;

import com.formdev.flatlaf.FlatClientProperties;
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

    private final JTextField usernameInputField = new JTextField(20);
    private final JPasswordField passwordInputField = new JPasswordField(20);
    private final JLabel errorLabel = new JLabel();
    private final JLabel infoLabel = new JLabel();

    private final JButton logIn;
    private final JButton signUp;
    private final LoginController loginController;
    private JPanel contentPanel;

    public LoginView(LoginViewModel loginViewModel, LoginController controller) {
        this.loginController = controller;
        this.loginViewModel = loginViewModel;
        this.loginViewModel.addPropertyChangeListener(this);

        setLayout(new GridBagLayout());
        
        // Main content panel
        contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor"), 1),
            BorderFactory.createEmptyBorder(100, 150, 100, 150)
        ));
        contentPanel.putClientProperty(FlatClientProperties.STYLE, "arc: 20; background: lighten($Panel.background, 3%)");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 20, 0);

        // Title
        JLabel title = new JLabel(LoginViewModel.TITLE_LABEL);
        title.putClientProperty(FlatClientProperties.STYLE, "font: bold +20");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(title, gbc);

        // Username
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 5, 0);
        JLabel usernameLabel = new JLabel(LoginViewModel.USERNAME_LABEL);
        usernameLabel.putClientProperty(FlatClientProperties.STYLE, "font: +5");
        contentPanel.add(usernameLabel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 15, 0);
        usernameInputField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your username");
        usernameInputField.putClientProperty(FlatClientProperties.STYLE, "showClearButton: true; font: +5");
        contentPanel.add(usernameInputField, gbc);

        // Password
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 5, 0);
        JLabel passwordLabel = new JLabel(LoginViewModel.PASSWORD_LABEL);
        passwordLabel.putClientProperty(FlatClientProperties.STYLE, "font: +5");
        contentPanel.add(passwordLabel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 20, 0);
        passwordInputField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your password");
        passwordInputField.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true; font: +5");
        contentPanel.add(passwordInputField, gbc);

        // Messages
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 10, 0);
        errorLabel.setForeground(new Color(255, 69, 58)); // Brighter red
        errorLabel.putClientProperty(FlatClientProperties.STYLE, "font: +2");
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(errorLabel, gbc);

        gbc.gridy++;
        infoLabel.setForeground(new Color(48, 209, 88)); // Brighter green
        infoLabel.putClientProperty(FlatClientProperties.STYLE, "font: +2");
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(infoLabel, gbc);

        // Buttons
        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 0, 0);
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        buttonPanel.setOpaque(false);

        logIn = new JButton(LoginViewModel.LOGIN_BUTTON_LABEL);
        logIn.putClientProperty(FlatClientProperties.STYLE, "font: bold +5");
        signUp = new JButton(LoginViewModel.SIGNUP_BUTTON_LABEL);
        signUp.putClientProperty(FlatClientProperties.STYLE, "font: bold +5");
        
        buttonPanel.add(logIn);
        buttonPanel.add(signUp);
        contentPanel.add(buttonPanel, gbc);

        // Add content panel to main view
        add(contentPanel);

        // Listeners
        logIn.addActionListener(this);
        signUp.addActionListener(this);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        SwingUtilities.getRootPane(this).setDefaultButton(logIn);
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
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (contentPanel != null) {
            contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor"), 1),
                BorderFactory.createEmptyBorder(100, 150, 100, 150)
            ));
        }
    }
}
