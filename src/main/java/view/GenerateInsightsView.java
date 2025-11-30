package view;

import com.formdev.flatlaf.FlatClientProperties;
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
    
    private final JTextField entityNameTextField;
    private final JComboBox<String> entityTypeComboBox;
    private final JTextArea insightTextArea;
    private final JButton generateButton;
    private final JButton backButton;
    private final JPanel outputContainer;
    private final Timer loadingTimer;
    private final JPanel formPanel;

    public GenerateInsightsView(GenerateInsightsViewModel generateInsightsViewModel, GenerateInsightsController controller) {
        this.generateInsightsController = controller;
        this.generateInsightsViewModel = generateInsightsViewModel;
        generateInsightsViewModel.addPropertyChangeListener(this);
        
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JLabel title = new JLabel(GenerateInsightsViewModel.TITLE_LABEL);
        title.putClientProperty(FlatClientProperties.STYLE, "font: bold +8");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        // Center Panel (Form + Output)
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Input Form Panel
        formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor"), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        formPanel.putClientProperty(FlatClientProperties.STYLE, "arc: 15; background: lighten($Panel.background, 3%)");
        
        GridBagConstraints fgbc = new GridBagConstraints();
        fgbc.fill = GridBagConstraints.HORIZONTAL;
        fgbc.insets = new Insets(5, 5, 5, 5);

        // Entity Type
        fgbc.gridx = 0; fgbc.gridy = 0;
        formPanel.add(new JLabel("Type:"), fgbc);
        
        entityTypeComboBox = new JComboBox<>(new String[]{"Player", "Team"});
        fgbc.gridx = 1; fgbc.weightx = 1.0;
        formPanel.add(entityTypeComboBox, fgbc);

        // Name
        fgbc.gridx = 0; fgbc.gridy = 1; fgbc.weightx = 0;
        formPanel.add(new JLabel("Name:"), fgbc);
        
        entityNameTextField = new JTextField(20);
        entityNameTextField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter player or team name");
        fgbc.gridx = 1; fgbc.weightx = 1.0;
        formPanel.add(entityNameTextField, fgbc);
        
        // Add Form to Center
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0;
        centerPanel.add(formPanel, gbc);

        // Output Area
        insightTextArea = new JTextArea(15, 50);
        insightTextArea.setEditable(false);
        insightTextArea.setLineWrap(true);
        insightTextArea.setWrapStyleWord(true);
        insightTextArea.putClientProperty(FlatClientProperties.STYLE, "font: 14");
        
        JScrollPane scrollPane = new JScrollPane(insightTextArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Clean look inside card
        
        outputContainer = new JPanel(new CardLayout());
        outputContainer.setBorder(BorderFactory.createTitledBorder("AI Analysis"));
        outputContainer.add(scrollPane, "CONTENT");
        
        JPanel loadingContainer = new JPanel(new GridBagLayout());
        loadingContainer.add(new LoadingPanel());
        outputContainer.add(loadingContainer, "LOADING");
        
        loadingTimer = new Timer(16, e -> outputContainer.repaint());

        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        centerPanel.add(outputContainer, gbc);

        add(centerPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backButton = new JButton(GenerateInsightsViewModel.BACK_BUTTON_LABEL);
        generateButton = new JButton(GenerateInsightsViewModel.GENERATE_BUTTON_LABEL);
        generateButton.putClientProperty(FlatClientProperties.STYLE, "font: bold; type: default");
        
        buttonPanel.add(backButton);
        buttonPanel.add(generateButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Listeners
        generateButton.addActionListener(this);
        backButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource().equals(generateButton)) {
            String entityName = entityNameTextField.getText();
            if (entityName == null || entityName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Player or team name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            generateInsightsController.execute(
                    entityName,
                    (String) entityTypeComboBox.getSelectedItem()
            );
        } else if (evt.getSource().equals(backButton)) {
            generateInsightsController.goBack();
        }
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
                    JOptionPane.showMessageDialog(this, state.getError(), "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    insightTextArea.setText(state.getInsight());
                    // Scroll to top
                    insightTextArea.setCaretPosition(0);
                }
            }
            if (!state.isLoading()) {
                 entityNameTextField.setText(state.getEntityName());
            }
        });
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (formPanel != null) {
            formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor"), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            ));
        }
    }
}
        