package view;

import com.formdev.flatlaf.FlatClientProperties;
import interface_adapter.signup.SignupController;
import interface_adapter.signup.SignupState;
import interface_adapter.signup.SignupViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class SignupView extends JPanel implements ActionListener, PropertyChangeListener {
    public final String viewName = "sign up";

    private final SignupViewModel signupViewModel;
    private final JTextField usernameInputField = new JTextField(20);
    private final JPasswordField passwordInputField = new JPasswordField(20);
    private final JPasswordField repeatPasswordInputField = new JPasswordField(20);
    private final SignupController signupController;

    private final JButton signUp;
    private final JButton cancel;
    private JPanel contentPanel;

    public SignupView(SignupController controller, SignupViewModel signupViewModel) {

        this.signupController = controller;
        this.signupViewModel = signupViewModel;
        signupViewModel.addPropertyChangeListener(this);

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
        JLabel title = new JLabel(SignupViewModel.TITLE_LABEL);
        title.putClientProperty(FlatClientProperties.STYLE, "font: bold +20");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(title, gbc);

        // Username
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 5, 0);
        JLabel usernameLabel = new JLabel(SignupViewModel.USERNAME_LABEL);
        usernameLabel.putClientProperty(FlatClientProperties.STYLE, "font: +5");
        contentPanel.add(usernameLabel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 15, 0);
        usernameInputField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Choose a username");
        usernameInputField.putClientProperty(FlatClientProperties.STYLE, "showClearButton: true; font: +5");
        contentPanel.add(usernameInputField, gbc);

        // Password
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 5, 0);
        JLabel passwordLabel = new JLabel(SignupViewModel.PASSWORD_LABEL);
        passwordLabel.putClientProperty(FlatClientProperties.STYLE, "font: +5");
        contentPanel.add(passwordLabel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 15, 0);
        passwordInputField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Create a password");
        passwordInputField.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true; font: +5");
        contentPanel.add(passwordInputField, gbc);

        // Repeat Password
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 5, 0);
        JLabel repeatPasswordLabel = new JLabel(SignupViewModel.REPEAT_PASSWORD_LABEL);
        repeatPasswordLabel.putClientProperty(FlatClientProperties.STYLE, "font: +5");
        contentPanel.add(repeatPasswordLabel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 20, 0);
        repeatPasswordInputField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Confirm your password");
        repeatPasswordInputField.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true; font: +5");
        contentPanel.add(repeatPasswordInputField, gbc);

        // Buttons
        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 0, 0);
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        buttonPanel.setOpaque(false);

        signUp = new JButton(SignupViewModel.SIGNUP_BUTTON_LABEL);
        signUp.putClientProperty(FlatClientProperties.STYLE, "font: bold +5");
        cancel = new JButton(SignupViewModel.CANCEL_BUTTON_LABEL);
        cancel.putClientProperty(FlatClientProperties.STYLE, "font: bold +5");

        buttonPanel.add(signUp);
        buttonPanel.add(cancel);
        contentPanel.add(buttonPanel, gbc);

        // Add content panel
        add(contentPanel);

        signUp.addActionListener(this);
        cancel.addActionListener(this);
    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource().equals(cancel)) {
            signupController.switchToLogin();
        } else if (evt.getSource().equals(signUp)) {
            SignupState currentState = signupViewModel.getState();

            String username = usernameInputField.getText();
            String password = new String(passwordInputField.getPassword());
            String repeatPassword = new String(repeatPasswordInputField.getPassword());

            currentState.setUsername(username);
            currentState.setPassword(password);
            currentState.setRepeatPassword(repeatPassword);
            signupViewModel.setState(currentState);

            signupController.execute(
                    currentState.getUsername(),
                    currentState.getPassword(),
                    currentState.getRepeatPassword()
            );
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SignupState state = (SignupState) evt.getNewValue();
        if (state.getError() != null) {
            JOptionPane.showMessageDialog(this, state.getError(), "Sign Up Error", JOptionPane.ERROR_MESSAGE);
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
