package view;

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
    private final JTextField usernameInputField = new JTextField(15);
    private final JPasswordField passwordInputField = new JPasswordField(15);
    private final JPasswordField repeatPasswordInputField = new JPasswordField(15);
    private final SignupController signupController;

    private final JButton signUp;
    private final JButton cancel;

    public SignupView(SignupController controller, SignupViewModel signupViewModel) {

        this.signupController = controller;
        this.signupViewModel = signupViewModel;
        signupViewModel.addPropertyChangeListener(this);

        // Styling
        Font titleFont = new Font("Arial", Font.BOLD, 24);
        Font fieldFont = new Font("Arial", Font.PLAIN, 18);
        Font buttonFont = new Font("Arial", Font.BOLD, 16);

        JLabel title = new JLabel(SignupViewModel.TITLE_LABEL);
        title.setFont(titleFont);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Setup fields
        usernameInputField.setFont(fieldFont);
        usernameInputField.setColumns(20);
        passwordInputField.setFont(fieldFont);
        passwordInputField.setColumns(20);
        repeatPasswordInputField.setFont(fieldFont);
        repeatPasswordInputField.setColumns(20);

        JLabel usernameLabel = new JLabel(SignupViewModel.USERNAME_LABEL);
        usernameLabel.setFont(fieldFont);
        JLabel passwordLabel = new JLabel(SignupViewModel.PASSWORD_LABEL);
        passwordLabel.setFont(fieldFont);
        JLabel repeatPasswordLabel = new JLabel(SignupViewModel.REPEAT_PASSWORD_LABEL);
        repeatPasswordLabel.setFont(fieldFont);

        LabelTextPanel usernameInfo = new LabelTextPanel(usernameLabel, usernameInputField);
        LabelTextPanel passwordInfo = new LabelTextPanel(passwordLabel, passwordInputField);
        LabelTextPanel repeatPasswordInfo = new LabelTextPanel(repeatPasswordLabel, repeatPasswordInputField);

        JPanel buttons = new JPanel();
        signUp = new JButton(SignupViewModel.SIGNUP_BUTTON_LABEL);
        signUp.setFont(buttonFont);
        signUp.setPreferredSize(new Dimension(120, 40));
        buttons.add(signUp);
        cancel = new JButton(SignupViewModel.CANCEL_BUTTON_LABEL);
        cancel.setFont(buttonFont);
        cancel.setPreferredSize(new Dimension(120, 40));
        buttons.add(cancel);

        signUp.addActionListener(this);
        cancel.addActionListener(this);

        // Layout
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        this.add(title, gbc);
        
        gbc.gridy++;
        this.add(usernameInfo, gbc);
        
        gbc.gridy++;
        this.add(passwordInfo, gbc);
        
        gbc.gridy++;
        this.add(repeatPasswordInfo, gbc);
        
        gbc.gridy++;
        this.add(buttons, gbc);
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
            JOptionPane.showMessageDialog(this, state.getError());
        }
    }
}
