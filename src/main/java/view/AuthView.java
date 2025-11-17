package view;

import interface_adapter.auth.AuthController;
import interface_adapter.auth.AuthState;
import interface_adapter.auth.AuthViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class AuthView extends JPanel implements ActionListener, PropertyChangeListener {
    public final String viewName = "auth";

    private final AuthViewModel authViewModel;
    private final AuthController authController;

    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JButton loginButton = new JButton("Login");
    private final JButton signupButton = new JButton("Sign up");
    private final JLabel feedbackLabel = new JLabel(" ");

    public AuthView(AuthViewModel authViewModel, AuthController authController) {
        this.authViewModel = authViewModel;
        this.authController = authController;
        authViewModel.addPropertyChangeListener(this);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(50, 80, 50, 80));

        JLabel title = new JLabel(AuthViewModel.TITLE);
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Arial", Font.PLAIN, 16);
        Font fieldFont = new Font("Arial", Font.PLAIN, 16);
        Font buttonFont = new Font("Arial", Font.BOLD, 16);

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(labelFont);
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(labelFont);
        feedbackLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        feedbackLabel.setForeground(Color.RED);
        feedbackLabel.setFont(labelFont);

        usernameField.setFont(fieldFont);
        passwordField.setFont(fieldFont);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 0));
        loginButton.addActionListener(this);
        signupButton.addActionListener(this);
        loginButton.setFont(buttonFont);
        signupButton.setFont(buttonFont);
        loginButton.setPreferredSize(new Dimension(120, 40));
        signupButton.setPreferredSize(new Dimension(120, 40));
        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);

        add(title);
        add(Box.createRigidArea(new Dimension(0, 30)));
        add(formPanel);
        add(Box.createRigidArea(new Dimension(0, 30)));
        add(buttonPanel);
        add(Box.createRigidArea(new Dimension(0, 30)));
        add(feedbackLabel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        if (e.getSource() == loginButton) {
            authController.login(username, password);
        } else if (e.getSource() == signupButton) {
            authController.signup(username, password);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        AuthState state = (AuthState) evt.getNewValue();
        if (state.getError() != null) {
            feedbackLabel.setForeground(Color.RED);
            feedbackLabel.setText(state.getError());
        } else if (state.getInfoMessage() != null) {
            feedbackLabel.setForeground(new Color(0, 128, 0));
            feedbackLabel.setText(state.getInfoMessage());
        } else {
            feedbackLabel.setText(" ");
        }
    }
}


