package view;

import interface_adapter.ask_question.AskQuestionController;
import interface_adapter.ask_question.AskQuestionViewModel;
import interface_adapter.compare_players.ComparePlayersController;
import interface_adapter.compare_players.ComparePlayersViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ChatView extends JPanel implements ActionListener, PropertyChangeListener {
    public final String viewName = "chat";

    private final AskQuestionViewModel askQuestionViewModel;
    private final AskQuestionController askQuestionController;
    private final ComparePlayersViewModel comparePlayersViewModel;
    private final ComparePlayersController comparePlayersController;

    private final JTextArea chatArea;
    private final JTextField inputField;
    private final JButton sendButton;
    private final JRadioButton askQuestionRadioButton;
    private final JRadioButton comparePlayersRadioButton;

    private enum Mode {
        ASK_QUESTION,
        COMPARE_PLAYERS
    }

    private Mode currentMode = Mode.ASK_QUESTION;

    public ChatView(AskQuestionViewModel askQuestionViewModel,
                    AskQuestionController askQuestionController,
                    ComparePlayersViewModel comparePlayersViewModel,
                    ComparePlayersController comparePlayersController) {
        this.askQuestionViewModel = askQuestionViewModel;
        this.askQuestionController = askQuestionController;
        this.comparePlayersViewModel = comparePlayersViewModel;
        this.comparePlayersController = comparePlayersController;

        askQuestionViewModel.addPropertyChangeListener(this);
        comparePlayersViewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Send");
        sendButton.addActionListener(this);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        JPanel modePanel = new JPanel();
        askQuestionRadioButton = new JRadioButton("Ask a Question", true);
        comparePlayersRadioButton = new JRadioButton("Compare Players");
        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(askQuestionRadioButton);
        modeGroup.add(comparePlayersRadioButton);
        modePanel.add(askQuestionRadioButton);
        modePanel.add(comparePlayersRadioButton);

        askQuestionRadioButton.addActionListener(e -> setMode(Mode.ASK_QUESTION));
        comparePlayersRadioButton.addActionListener(e -> setMode(Mode.COMPARE_PLAYERS));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(modePanel, BorderLayout.NORTH);
        bottomPanel.add(inputPanel, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);

        setMode(Mode.ASK_QUESTION);
    }

    private void setMode(Mode mode) {
        currentMode = mode;
        if (mode == Mode.ASK_QUESTION) {
            inputField.setToolTipText("Enter your question here");
        } else {
            inputField.setToolTipText("Enter two player names, separated by a comma (e.g., LeBron James, Michael Jordan)");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sendButton) {
            String inputText = inputField.getText();
            if (currentMode == Mode.ASK_QUESTION) {
                askQuestionController.execute(inputText);
                chatArea.append("You: " + inputText + "\n");
            } else {
                String[] playerNames = inputText.split(",");
                if (playerNames.length == 2) {
                    comparePlayersController.execute(playerNames[0].trim(), playerNames[1].trim());
                    chatArea.append("You: Compare " + playerNames[0].trim() + " and " + playerNames[1].trim() + "\n");
                } else {
                    JOptionPane.showMessageDialog(this, "Please enter two player names separated by a comma.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            }
            inputField.setText("");
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())) {
            if (evt.getSource() == askQuestionViewModel) {
                String answer = askQuestionViewModel.getState().getAnswer();
                if (answer != null && !answer.isEmpty()) {
                    chatArea.append("AI: " + answer + "\n");
                }
                String error = askQuestionViewModel.getState().getError();
                if (error != null) {
                    chatArea.append("AI Error: " + error + "\n");
                }
            } else if (evt.getSource() == comparePlayersViewModel) {
                String comparison = comparePlayersViewModel.getState().getComparison();
                if (comparison != null && !comparison.isEmpty()) {
                    chatArea.append("AI: " + comparison + "\n");
                }
                String error = comparePlayersViewModel.getState().getError();
                if (error != null) {
                    chatArea.append("AI Error: " + error + "\n");
                }
            }
        }
    }
}
