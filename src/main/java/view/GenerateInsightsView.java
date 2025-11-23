package view;

import interface_adapter.generate_insights.GenerateInsightsController;
import interface_adapter.generate_insights.GenerateInsightsState;
import interface_adapter.generate_insights.GenerateInsightsViewModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class GenerateInsightsView extends JPanel implements ActionListener, PropertyChangeListener {
    public final String viewName = "generate_insights";
    private final GenerateInsightsViewModel generateInsightsViewModel;
    private final GenerateInsightsController generateInsightsController;
    private final JTextField entityNameTextField = new JTextField(20);
    private final JComboBox<String> entityTypeComboBox;
    private final JTextArea insightTextArea = new JTextArea(15, 50);
    private final JButton generateButton;
            private final JButton backButton;
            private final JPanel outputContainer;
            private final Timer loadingTimer;
        
            public GenerateInsightsView(GenerateInsightsViewModel generateInsightsViewModel, GenerateInsightsController controller) {
                this.generateInsightsController = controller;
                        this.generateInsightsViewModel = generateInsightsViewModel;
                        generateInsightsViewModel.addPropertyChangeListener(this);
                
                        this.setLayout(new BorderLayout());
                        this.setBorder(new EmptyBorder(20, 20, 20, 20)); // Proper margins        
                JLabel title = new JLabel(GenerateInsightsViewModel.TITLE_LABEL);
                title.setAlignmentX(Component.CENTER_ALIGNMENT);
                title.setFont(new Font("Arial", Font.BOLD, 24)); // Larger title
                title.setHorizontalAlignment(SwingConstants.CENTER);
                this.add(title, BorderLayout.NORTH);
        
                JPanel centerPanel = new JPanel(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(10, 10, 10, 10); // Spacing between components
                gbc.fill = GridBagConstraints.HORIZONTAL;
        
                // Row 0: Entity Type
                JLabel typeLabel = new JLabel("Entity Type:");
                typeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.weightx = 0;
                centerPanel.add(typeLabel, gbc);
        
                entityTypeComboBox = new JComboBox<>(new String[]{"Player", "Team"});
                entityTypeComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
                gbc.gridx = 1;
                gbc.gridy = 0;
                gbc.weightx = 1.0;
                centerPanel.add(entityTypeComboBox, gbc);
        
                // Row 1: Name
                JLabel nameLabel = new JLabel("Name:");
                nameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
                gbc.gridx = 0;
                gbc.gridy = 1;
                gbc.weightx = 0;
                centerPanel.add(nameLabel, gbc);
        
                entityNameTextField.setFont(new Font("Arial", Font.PLAIN, 16));
                gbc.gridx = 1;
                gbc.gridy = 1;
                gbc.weightx = 1.0;
                centerPanel.add(entityNameTextField, gbc);
        
                // Row 2: Output Area
                insightTextArea.setEditable(false);
                insightTextArea.setLineWrap(true);
                insightTextArea.setWrapStyleWord(true);
                insightTextArea.setFont(new Font("Arial", Font.PLAIN, 14));
                JScrollPane scrollPane = new JScrollPane(insightTextArea);
                scrollPane.setBorder(BorderFactory.createTitledBorder("AI Analysis"));
                
                        // Create a container with CardLayout to switch between content and loading
                        outputContainer = new JPanel(new CardLayout());
                        outputContainer.add(scrollPane, "CONTENT");
                        
                        // Timer for 60 FPS animation of loading bubble - initialized after outputContainer
                        loadingTimer = new Timer(16, e -> outputContainer.repaint());
                        
                        JPanel loadingContainer = new JPanel(new GridBagLayout());                loadingContainer.add(new LoadingPanel());
                outputContainer.add(loadingContainer, "LOADING");
        
                gbc.gridx = 0;
                gbc.gridy = 2;
                gbc.gridwidth = 2;
                gbc.weighty = 1.0; // Give it vertical space
                gbc.fill = GridBagConstraints.BOTH;
                centerPanel.add(outputContainer, gbc);
        
                this.add(centerPanel, BorderLayout.CENTER);
        
                // Buttons
                JPanel buttons = new JPanel();
                generateButton = new JButton(GenerateInsightsViewModel.GENERATE_BUTTON_LABEL);
                generateButton.setFont(new Font("Arial", Font.BOLD, 16));
                buttons.add(generateButton);
                
                backButton = new JButton(GenerateInsightsViewModel.BACK_BUTTON_LABEL);
                backButton.setFont(new Font("Arial", Font.BOLD, 16));
                buttons.add(backButton);
        
                this.add(buttons, BorderLayout.SOUTH);
        
                generateButton.addActionListener(
                        new ActionListener() {
                            public void actionPerformed(ActionEvent evt) {
                                if (evt.getSource().equals(generateButton)) {
                                    String entityName = entityNameTextField.getText();
                                    if (entityName == null || entityName.trim().isEmpty()) {
                                        JOptionPane.showMessageDialog(GenerateInsightsView.this, "Player or team name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                                        return;
                                    }
                                    generateInsightsController.execute(
                                            entityName,
                                            (String) entityTypeComboBox.getSelectedItem()
                                    );
                                }
                            }
                        }
                );
        
                backButton.addActionListener(
                    new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            if (evt.getSource().equals(backButton)) {
                                generateInsightsController.goBack();
                            }
                        }
                    }
                );
            }
        
            @Override
            public void actionPerformed(ActionEvent e) {
                // This is for future use, if other actions are added.
            }
        
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                GenerateInsightsState state = (GenerateInsightsState) evt.getNewValue();
                SwingUtilities.invokeLater(() -> {
                    CardLayout cl = (CardLayout) outputContainer.getLayout();
                    if (state.isLoading()) {
                        if (!loadingTimer.isRunning()) loadingTimer.start();
                        generateButton.setEnabled(false);
                        entityNameTextField.setEnabled(false);
                        cl.show(outputContainer, "LOADING");
                    } else {
                        if (loadingTimer.isRunning()) loadingTimer.stop();
                        generateButton.setEnabled(true);
                        entityNameTextField.setEnabled(true);
                        cl.show(outputContainer, "CONTENT");
                        if (state.getError() != null) {
                            JOptionPane.showMessageDialog(this, state.getError());
                        } else {
                            insightTextArea.setText(state.getInsight());
                        }
                    }
                    // Only update text field if we are not loading, or if it's a fresh state update that might change it
                    // But usually we don't want to overwrite user input while they are typing if it's just a background refresh
                    // Here, state update usually comes from "Submit" or "Back".
                    // If loading, we disabled it, so it's safe.
                    if (!state.isLoading()) {
                         entityNameTextField.setText(state.getEntityName());
                    }
                });
            }
        }

        