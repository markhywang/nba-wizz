package view.compare;

import entity.Normalization;
import interface_adapter.compare.CompareController;
import interface_adapter.compare.CompareViewModel;
import interface_adapter.compare.CompareState;
import view.ViewManager;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class CompareView extends JPanel implements PropertyChangeListener {

    public final String viewName = "Compare";

    private final CompareViewModel viewModel;
    private final CompareController compareController;

    private final JRadioButton playersButton = new JRadioButton("Players", true);
    private final JRadioButton teamsButton = new JRadioButton("Teams");
    private final JTextField nameField = new JTextField();

    private final JTextField seasonStartField = new JTextField("1980");
    private final JTextField seasonEndField = new JTextField("2024");

    private final JComboBox<String> presetBox = new JComboBox<>(new String[]{"Basic", "Efficiency"});

    private final JComboBox<Normalization> normBox = new JComboBox<>(new Normalization[]{Normalization.PER_GAME, Normalization.PER_36});

    private final JButton compareButton = new JButton("Compare");

    private final JLabel seasonLabel = new JLabel("Season(s): -");
    private final JTable table = new JTable();
    private final JTextArea noticesArea = new JTextArea(3, 40);
    private final JTextArea insightArea = new JTextArea(2, 40);

    public CompareView(CompareViewModel viewModel, CompareController compareController) {
        this.viewModel = viewModel;
        this.compareController = compareController;
        this.viewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout(8, 8));

        JPanel top = new JPanel(new GridLayout(3, 1, 4, 4));

        JPanel r1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(playersButton); buttonGroup.add(teamsButton);
        r1.add(playersButton);
        r1.add(teamsButton);
        r1.add(new JLabel("Names (comma-separated): "));
        nameField.setColumns(30);
        r1.add(nameField);
        top.add(r1);

        JPanel r2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        r2.add(new JLabel("Seasons: "));
        seasonStartField.setColumns(5);
        seasonEndField.setColumns(5);
        r2.add(seasonStartField);
        r2.add(new JLabel(" to "));
        r2.add(seasonEndField);
        r2.add(new JLabel(" Preset: "));
        r2.add(presetBox);
        r2.add(new JLabel(" Normalize: "));
        r2.add(normBox);
        top.add(r2);

        JPanel r3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        r3.add(compareButton);
        top.add(r3);

        add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(4, 4));
        center.add(seasonLabel, BorderLayout.NORTH);
        table.setFillsViewportHeight(true);
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new GridLayout(2, 1, 4, 4));
        insightArea.setEditable(false);
        noticesArea.setEditable(false);
        bottom.add(new JScrollPane(noticesArea));
        bottom.add(new JScrollPane(insightArea));
        add(bottom, BorderLayout.SOUTH);

        compareButton.addActionListener(e -> onCompareClicked());
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

        if (state.error != null) {
            JOptionPane.showMessageDialog(this, state.error, "Compare", JOptionPane.ERROR_MESSAGE);
        }

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Metric");
        for (String name : state.entities) {
            model.addColumn(name);
        }

        for (CompareState.RowVM row: state.table) {
            List<Object> cells = new ArrayList<>();
            cells.add(row.metric);
            cells.addAll(row.cells);
            model.addRow(cells.toArray());
        }
        table.setModel(model);

        seasonLabel.setText("Season(s): " + state.seasonLabel);
        noticesArea.setText(String.join("\n", state.notices));
        insightArea.setText(state.insight == null ? "" : state.insight);

    }
}
