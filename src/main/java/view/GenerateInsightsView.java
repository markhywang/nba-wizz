package view;

import interface_adapter.generate_insights.GenerateInsightsController;
import interface_adapter.generate_insights.GenerateInsightsState;
import interface_adapter.generate_insights.GenerateInsightsViewModel;
import interface_adapter.main_menu.MainMenuViewModel;

import javax.swing.*;
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
    private final JTextArea insightTextArea = new JTextArea(10, 40);
    private final JButton generateButton;
    private final JButton backButton;

    public GenerateInsightsView(GenerateInsightsViewModel generateInsightsViewModel, GenerateInsightsController controller) {
        this.generateInsightsController = controller;
        this.generateInsightsViewModel = generateInsightsViewModel;
        generateInsightsViewModel.addPropertyChangeListener(this);

        JLabel title = new JLabel(GenerateInsightsViewModel.TITLE_LABEL);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Entity Type:"));
        entityTypeComboBox = new JComboBox<>(new String[]{"Player", "Team"});
        inputPanel.add(entityTypeComboBox);
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(entityNameTextField);

        insightTextArea.setEditable(false);
        insightTextArea.setLineWrap(true);
        insightTextArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(insightTextArea);

        JPanel buttons = new JPanel();
        generateButton = new JButton(GenerateInsightsViewModel.GENERATE_BUTTON_LABEL);
        buttons.add(generateButton);
        backButton = new JButton(GenerateInsightsViewModel.BACK_BUTTON_LABEL);
        buttons.add(backButton);

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

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(title);
        this.add(inputPanel);
        this.add(scrollPane);
        this.add(buttons);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // This is for future use, if other actions are added.
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        GenerateInsightsState state = (GenerateInsightsState) evt.getNewValue();
        if (state.getError() != null) {
            JOptionPane.showMessageDialog(this, state.getError());
        } else {
            insightTextArea.setText(state.getInsight());
        }
        entityNameTextField.setText(state.getEntityName());
    }
}
