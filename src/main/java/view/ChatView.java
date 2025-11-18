package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.ask_question.AskQuestionController;
import interface_adapter.ask_question.AskQuestionState;
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
    private final ViewManagerModel viewManagerModel;

    private final DefaultListModel<ChatMessage> chatModel;
    private final JList<ChatMessage> chatList;
    private final JTextField inputField;
    private final JButton sendButton;
    private final JButton homeButton;
    private final JRadioButton askQuestionRadioButton;
    private final JRadioButton comparePlayersRadioButton;
    private final JLabel loadingIndicator;
    private static final Font INPUT_FONT = new Font("Arial", Font.PLAIN, 16);


    private enum Mode {
        ASK_QUESTION,
        COMPARE_PLAYERS
    }

    private Mode currentMode = Mode.ASK_QUESTION;

    public ChatView(AskQuestionViewModel askQuestionViewModel,
                    AskQuestionController askQuestionController,
                    ComparePlayersViewModel comparePlayersViewModel,
                    ComparePlayersController comparePlayersController,
                    ViewManagerModel viewManagerModel) {
        this.askQuestionViewModel = askQuestionViewModel;
        this.askQuestionController = askQuestionController;
        this.comparePlayersViewModel = comparePlayersViewModel;
        this.comparePlayersController = comparePlayersController;
        this.viewManagerModel = viewManagerModel;

        askQuestionViewModel.addPropertyChangeListener(this);
        comparePlayersViewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());

        // Top panel for Home button and loading indicator
        JPanel topPanel = new JPanel(new BorderLayout());
        homeButton = new JButton("Home");
        homeButton.setFont(INPUT_FONT);
        homeButton.addActionListener(this);
        topPanel.add(homeButton, BorderLayout.WEST);

        loadingIndicator = new JLabel("Loading...", SwingConstants.CENTER);
        loadingIndicator.setFont(INPUT_FONT);
        loadingIndicator.setVisible(false);
        topPanel.add(loadingIndicator, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);


        chatModel = new DefaultListModel<>();
        chatList = new JList<>(chatModel);
        chatList.setCellRenderer(new ChatBubbleCellRenderer());
        JScrollPane scrollPane = new JScrollPane(chatList);
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inputField = new JTextField();
        inputField.setFont(INPUT_FONT);
        sendButton = new JButton("Send");
        sendButton.setFont(INPUT_FONT);
        sendButton.addActionListener(this);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.setPreferredSize(new Dimension(0, 60));


        JPanel modePanel = new JPanel();
        askQuestionRadioButton = new JRadioButton("Ask a Question", true);
        askQuestionRadioButton.setFont(INPUT_FONT);
        comparePlayersRadioButton = new JRadioButton("Compare Players");
        comparePlayersRadioButton.setFont(INPUT_FONT);
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
                                    chatModel.addElement(new ChatMessage(inputText, ChatMessage.Sender.USER));
                                    chatList.ensureIndexIsVisible(chatModel.getSize() - 1);

                                    AskQuestionState currentState = askQuestionViewModel.getState();
                                    currentState.setAnswer(""); // Clear the old answer before starting a new question
                                    currentState.setError(null); // Clear any previous errors
                                    currentState.setLoading(true);
                                    askQuestionViewModel.firePropertyChanged();

                                    askQuestionController.execute(inputText);            } else {
                String[] playerNames = inputText.split(",");
                if (playerNames.length == 2) {
                    String message = "Compare " + playerNames[0].trim() + " and " + playerNames[1].trim();
                    chatModel.addElement(new ChatMessage(message, ChatMessage.Sender.USER));
                    comparePlayersController.execute(playerNames[0].trim(), playerNames[1].trim());
                } else {
                    JOptionPane.showMessageDialog(this, "Please enter two player names separated by a comma.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            }
            inputField.setText("");
        } else if (e.getSource() == homeButton) {
            viewManagerModel.setActiveView("main_menu");
            viewManagerModel.firePropertyChanged();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())) {
            if (evt.getSource() == askQuestionViewModel) {
                AskQuestionState state = askQuestionViewModel.getState();
                loadingIndicator.setVisible(state.isLoading());

                String answer = state.getAnswer();
                // Only display the answer when loading is complete (non-streaming)
                if (answer != null && !answer.isEmpty() && !state.isLoading()) {
                    // If the last message is from the user, create a new AI message bubble
                    // Otherwise, update the existing AI message
                    if (chatModel.isEmpty() || chatModel.lastElement().getSender() == ChatMessage.Sender.USER) {
                        chatModel.addElement(new ChatMessage(answer, ChatMessage.Sender.AI));
                    } else {
                        // Last element is an AI message, update it
                        chatModel.lastElement().setText(answer);
                    }
                    chatList.ensureIndexIsVisible(chatModel.getSize() - 1);
                    chatList.repaint();
                }


                String error = state.getError();
                if (error != null) {
                    chatModel.addElement(new ChatMessage("Error: " + error, ChatMessage.Sender.AI));
                    chatList.ensureIndexIsVisible(chatModel.getSize() - 1);
                }
            } else if (evt.getSource() == comparePlayersViewModel) {
                String comparison = comparePlayersViewModel.getState().getComparison();
                if (comparison != null && !comparison.isEmpty()) {
                    chatModel.addElement(new ChatMessage(comparison, ChatMessage.Sender.AI));
                    chatList.ensureIndexIsVisible(chatModel.getSize() - 1);
                }
                String error = comparePlayersViewModel.getState().getError();
                if (error != null) {
                    chatModel.addElement(new ChatMessage("Error: " + error, ChatMessage.Sender.AI));
                    chatList.ensureIndexIsVisible(chatModel.getSize() - 1);
                }
            }
        }
    }
}
