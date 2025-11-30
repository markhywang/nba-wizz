package view.compare;

import com.formdev.flatlaf.FlatClientProperties;
import entity.Normalization;
import interface_adapter.compare.CompareController;
import interface_adapter.compare.CompareViewModel;
import interface_adapter.compare.CompareState;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import com.formdev.flatlaf.FlatLaf;

public class CompareView extends JPanel implements PropertyChangeListener {

    public final String viewName = "Compare";

    private final CompareViewModel viewModel;
    private final CompareController compareController;

    private CompareState currentState;
    private JPanel controlPanel;

    private final JRadioButton playersButton = new JRadioButton("Players", true);
    private final JRadioButton teamsButton = new JRadioButton("Teams");
    private final JTextField nameField = new JTextField();

    private final JTextField seasonStartField = new JTextField("1980");
    private final JTextField seasonEndField = new JTextField("2024");

    private final JComboBox<String> presetBox = new JComboBox<>(new String[]{"Basic", "Efficiency"});
    private final JComboBox<Normalization> normBox = new JComboBox<>(new Normalization[]{Normalization.PER_GAME, Normalization.PER_36});

    private final JButton compareButton = new JButton("Compare");
    private final JButton homeButton = new JButton("Home");

    private final JLabel seasonLabel = new JLabel("Season(s): -");
    private final JTable table = new JTable();
    private final JTextArea noticesArea = new JTextArea(3, 40);
    private final JTextArea insightArea = new JTextArea(2, 40);

    public CompareView(CompareController compareController, CompareViewModel viewModel) {
        this.viewModel = viewModel;
        this.compareController = compareController;
        this.viewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Head-to-Head Comparison");
        title.putClientProperty(FlatClientProperties.STYLE, "font: bold +8");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        
        homeButton.putClientProperty(FlatClientProperties.STYLE, "buttonType: roundRect");
        headerPanel.add(homeButton, BorderLayout.WEST);
        headerPanel.add(title, BorderLayout.CENTER);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(headerPanel, BorderLayout.NORTH);

        // Control Panel
        controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor"), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        controlPanel.putClientProperty(FlatClientProperties.STYLE, "arc: 15; background: lighten($Panel.background, 3%)");
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Row 1: Type Selection
        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(new JLabel("Compare:"), gbc);
        
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(playersButton); buttonGroup.add(teamsButton);
        radioPanel.add(playersButton);
        radioPanel.add(Box.createHorizontalStrut(10));
        radioPanel.add(teamsButton);
        gbc.gridx = 1; gbc.gridwidth = 3;
        controlPanel.add(radioPanel, gbc);

        // Row 2: Names
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        controlPanel.add(new JLabel("Names:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 3;
        nameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "e.g. LeBron James, Michael Jordan (comma separated)");
        controlPanel.add(nameField, gbc);

        // Row 3: Seasons & Options
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        controlPanel.add(new JLabel("Season Range:"), gbc);
        
        JPanel seasonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        seasonStartField.setColumns(5);
        seasonEndField.setColumns(5);
        seasonPanel.add(seasonStartField);
        seasonPanel.add(new JLabel(" - "));
        seasonPanel.add(seasonEndField);
        gbc.gridx = 1;
        controlPanel.add(seasonPanel, gbc);
        
        gbc.gridx = 2;
        controlPanel.add(new JLabel("Stats Preset:"), gbc);
        gbc.gridx = 3;
        controlPanel.add(presetBox, gbc);
        
        // Row 4: More Options & Button
        gbc.gridx = 0; gbc.gridy = 3;
        controlPanel.add(new JLabel("Normalization:"), gbc);
        gbc.gridx = 1;
        controlPanel.add(normBox, gbc);
        
        compareButton.putClientProperty(FlatClientProperties.STYLE, "font: bold; type: default");
        gbc.gridx = 3;
        controlPanel.add(compareButton, gbc);

        // Add Control Panel
        add(controlPanel, BorderLayout.CENTER); // Will sit between Header and Results (Center actually expands, so we might want to put this in NORTH too, but let's use a main container)

        // Main Container for Center content
        JPanel mainContent = new JPanel(new BorderLayout(0, 10));
        mainContent.add(controlPanel, BorderLayout.NORTH);
        
        // Results Section
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBorder(BorderFactory.createTitledBorder("Results"));
        
        seasonLabel.putClientProperty(FlatClientProperties.STYLE, "font: bold");
        seasonLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        resultsPanel.add(seasonLabel, BorderLayout.NORTH);
        
        table.setFillsViewportHeight(true);
        resultsPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        mainContent.add(resultsPanel, BorderLayout.CENTER);
        
        add(mainContent, BorderLayout.CENTER);


        // Bottom Info
        JPanel bottom = new JPanel(new GridLayout(2, 1, 10, 10));
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JScrollPane noticesScroll = new JScrollPane(noticesArea);
        noticesScroll.setBorder(BorderFactory.createTitledBorder("Notices"));
        noticesArea.setEditable(false);
        noticesArea.setLineWrap(true);
        
        JScrollPane insightScroll = new JScrollPane(insightArea);
        insightScroll.setBorder(BorderFactory.createTitledBorder("AI Insight"));
        insightArea.setEditable(false);
        insightArea.setLineWrap(true);

        bottom.add(noticesScroll);
        bottom.add(insightScroll);
        
        add(bottom, BorderLayout.SOUTH);

        compareButton.addActionListener(e -> onCompareClicked());
        homeButton.addActionListener(e -> compareController.switchToMainMenu());
    }

    private void onCompareClicked() {
        List<String> names = new ArrayList<>();
        for (String s : nameField.getText().split(",")) {
            String t = s.trim();
            if (!t.isEmpty()) names.add(t);
        }

        int start = parseIntOr(seasonStartField.getText(), 1980);
        int end = parseIntOr(seasonEndField.getText(), 2024);
        String preset = (String) presetBox.getSelectedItem();
        Normalization norm = (Normalization) normBox.getSelectedItem();

        if (playersButton.isSelected()) {
            compareController.comparePlayers(names, start, end, preset, norm);
        } else {
            compareController.compareTeams(names, start, end, preset, norm);
        }
    }

    private int parseIntOr(String s, int defaultValue) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!"state".equals(evt.getPropertyName())) return;
        CompareState state = (CompareState) evt.getNewValue();
        currentState = state;

        if (state.error != null) {
            JOptionPane.showMessageDialog(this, state.error, "Compare Error", JOptionPane.ERROR_MESSAGE);
        }

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Metric");
        for (String name : state.entities) {
            model.addColumn(name);
        }

        if (state.table != null) {
            for (CompareState.RowVM row : state.table) {
                List<Object> cells = new ArrayList<>();
                cells.add(row.metric());
                cells.addAll(row.cells());
                model.addRow(cells.toArray());
            }
        }
        table.setModel(model);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Reset to default colors first (FlatLaf handles selection automatically if we don't force it)
                if (!isSelected) {
                     c.setBackground(table.getBackground());
                     c.setForeground(table.getForeground());
                }
                
                // Highlight best value
                // column 0 is metric name, columns 1..N are values
                // bestIndex is 0-based index into values list (so matches column-1)
                if (currentState != null && currentState.table != null && row >= 0 && row < currentState.table.size()) {
                    CompareState.RowVM rowVM = currentState.table.get(row);
                    if (rowVM != null && rowVM.bestIndex() != null && column - 1 == rowVM.bestIndex()) {
                        if (!isSelected) {
                             // Use a nice green for "Best"
                             Color green = FlatLaf.isLafDark() ? new Color(40, 100, 40) : new Color(40, 167, 69);
                             c.setBackground(green); 
                             c.setForeground(Color.WHITE);
                        } else {
                            // If selected, keep selection background but maybe bold text
                        }
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    }
                }
                return c;
            }
        });
        
        seasonLabel.setText("Season(s): " + (state.seasonLabel != null ? state.seasonLabel : "-"));
        noticesArea.setText(state.notices != null ? String.join("\n", state.notices) : "");
        insightArea.setText(state.insight != null ? state.insight : "");
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (controlPanel != null) {
            controlPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor"), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            ));
        }
    }
}
