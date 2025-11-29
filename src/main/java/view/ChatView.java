package view;

import com.formdev.flatlaf.FlatClientProperties;
import interface_adapter.ViewManagerModel;
import interface_adapter.ask_question.AskQuestionController;
import interface_adapter.ask_question.AskQuestionState;
import interface_adapter.ask_question.AskQuestionViewModel;
import interface_adapter.compare_players.ComparePlayersController;
import interface_adapter.compare_players.ComparePlayersState;
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
    private final Timer loadingTimer;

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

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel title = new JLabel("AI Basketball Assistant");
        title.putClientProperty(FlatClientProperties.STYLE, "font: bold +16");
        title.setForeground(new Color(37, 99, 235)); // Modern Blue
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitle = new JLabel("Ask anything or compare players instantly");
        subtitle.putClientProperty(FlatClientProperties.STYLE, "font: 0; foreground: $Label.disabledForeground");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel topRow = new JPanel(new BorderLayout());
        homeButton = new JButton("Home");
        homeButton.putClientProperty(FlatClientProperties.STYLE, "buttonType: roundRect");
        homeButton.addActionListener(this);
        topRow.add(homeButton, BorderLayout.WEST);
        
        // Wrap title/subtitle to center them relative to the panel, not just available space
        JPanel titleWrapper = new JPanel();
        titleWrapper.setLayout(new BoxLayout(titleWrapper, BoxLayout.Y_AXIS));
        titleWrapper.add(title);
        titleWrapper.add(Box.createVerticalStrut(2));
        titleWrapper.add(subtitle);
        
        // This is a bit of a hack to center the title while having a button on the left
        // A better way is a 3-column grid or overlay, but this is simple:
        // We add the titleWrapper to the center of topRow.
        // However, standard BorderLayout center isn't perfectly centered if West/East differ.
        // For simplicity in this CLI context, we'll just stack: Home button top-left, Title centered below.
        
        JPanel realHeader = new JPanel(new BorderLayout());
        realHeader.add(homeButton, BorderLayout.WEST);
        
        JPanel centerTitlePanel = new JPanel();
        centerTitlePanel.setLayout(new BoxLayout(centerTitlePanel, BoxLayout.Y_AXIS));
        centerTitlePanel.add(title);
        centerTitlePanel.add(subtitle);
        
        realHeader.add(centerTitlePanel, BorderLayout.CENTER);
        realHeader.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        add(realHeader, BorderLayout.NORTH);

        // Chat Area
        chatModel = new DefaultListModel<>();
        chatList = new JList<>(chatModel);
        chatList.setCellRenderer(new ChatBubbleCellRenderer());
        chatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(chatList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
        
        loadingTimer = new Timer(16, e -> chatList.repaint());

        // Bottom Control Area
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));

        // Mode Selection
        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        askQuestionRadioButton = new JRadioButton("Ask Question", true);
        comparePlayersRadioButton = new JRadioButton("Compare Players");
        
        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(askQuestionRadioButton);
        modeGroup.add(comparePlayersRadioButton);
        
        modePanel.add(askQuestionRadioButton);
        modePanel.add(Box.createHorizontalStrut(10));
        modePanel.add(comparePlayersRadioButton);

        askQuestionRadioButton.addActionListener(e -> setMode(Mode.ASK_QUESTION));
        comparePlayersRadioButton.addActionListener(e -> setMode(Mode.COMPARE_PLAYERS));

        // Input Area
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputField = new JTextField();
        inputField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your NBA question here...");
        // Increased padding to make it taller
        inputField.putClientProperty(FlatClientProperties.STYLE, "arc: 15; padding: 10,10,10,10; font: +2");
        
        sendButton = new JButton("Send");
        // Increased font size and potentially padding via style
        sendButton.putClientProperty(FlatClientProperties.STYLE, "font: bold +2; type: default; margin: 5,15,5,15");
        sendButton.setPreferredSize(new Dimension(100, 45)); // Explicit height bump
        sendButton.addActionListener(this);

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        
        bottomPanel.add(modePanel, BorderLayout.NORTH);
        bottomPanel.add(inputPanel, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);

        setMode(Mode.ASK_QUESTION);
    }

    private void setMode(Mode mode) {
        currentMode = mode;
        if (mode == Mode.ASK_QUESTION) {
            inputField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your NBA question here...");
            inputField.setToolTipText("Enter your question here");
        } else {
            inputField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "e.g. LeBron James, Michael Jordan");
            inputField.setToolTipText("Enter two player names, separated by a comma");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sendButton) {
            String inputText = inputField.getText();
            if (inputText.trim().isEmpty()) return;

            if (currentMode == Mode.ASK_QUESTION) {
                chatModel.addElement(new ChatMessage(inputText, ChatMessage.Sender.USER));
                scrollToBottom();

                AskQuestionState currentState = askQuestionViewModel.getState();
                currentState.setAnswer(""); 
                currentState.setError(null);
                currentState.setLoading(true);
                askQuestionViewModel.firePropertyChanged();

                askQuestionController.execute(inputText);            
            } else {
                String[] playerNames = inputText.split(",");
                if (playerNames.length == 2) {
                    String message = "Compare " + playerNames[0].trim() + " and " + playerNames[1].trim();
                    chatModel.addElement(new ChatMessage(message, ChatMessage.Sender.USER));
                    scrollToBottom();
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
    
    private void scrollToBottom() {
        chatList.ensureIndexIsVisible(chatModel.getSize() - 1);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(() -> {
            if ("state".equals(evt.getPropertyName())) {
                if (evt.getSource() == askQuestionViewModel) {
                    AskQuestionState state = askQuestionViewModel.getState();
                    handleStateUpdate(state.isLoading(), state.getAnswer(), state.getError());
                } else if (evt.getSource() == comparePlayersViewModel) {
                    ComparePlayersState state = comparePlayersViewModel.getState();
                    handleStateUpdate(state.isLoading(), state.getComparison(), state.getError());
                }
            }
        });
    }

    private void handleStateUpdate(boolean isLoading, String response, String error) {
        if (isLoading) {
            if (!loadingTimer.isRunning()) loadingTimer.start();
            inputField.setEnabled(false);
            sendButton.setEnabled(false);

            if (chatModel.isEmpty() || chatModel.lastElement().getSender() != ChatMessage.Sender.LOADING) {
                chatModel.addElement(new ChatMessage("", ChatMessage.Sender.LOADING));
                scrollToBottom();
            }
        } else {
            if (loadingTimer.isRunning()) loadingTimer.stop();
            inputField.setEnabled(true);
            sendButton.setEnabled(true);
            inputField.requestFocusInWindow();

            // Remove loading bubble
            if (!chatModel.isEmpty() && chatModel.lastElement().getSender() == ChatMessage.Sender.LOADING) {
                chatModel.removeElementAt(chatModel.getSize() - 1);
            }

            if (response != null && !response.isEmpty()) {
                chatModel.addElement(new ChatMessage(response, ChatMessage.Sender.AI));
                scrollToBottom();
            }

            if (error != null) {
                chatModel.addElement(new ChatMessage("Error: " + error, ChatMessage.Sender.AI));
                scrollToBottom();
            }
            chatList.repaint();
        }
    }
}
