package view;

import interface_adapter.sort_players.SortController;
import interface_adapter.sort_players.SortViewModel;
import interface_adapter.sort_players.SortState;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public class SortPlayersView extends JPanel implements PropertyChangeListener {

    public final String viewName = "sort_players";

    private final SortController controller;
    private final SortViewModel viewModel;

    private final JTable table;
    private final DefaultTableModel tableModel;

    private final JComboBox<String> positionComboBox;
    private final JTextField teamField;
    private final JTextField seasonFromField;
    private final JTextField seasonToField;

    // 17 columns, same order as CSV
    private static final String[] COLUMN_NAMES = {
            "Name", "Pos", "Age", "Team", "Season",
            "G", "MP", "FG%", "3P%", "FT%",
            "TRB", "AST", "TOV", "STL", "BLK", "PF", "PTS"
    };

    public SortPlayersView(SortController controller, SortViewModel viewModel) {
        this.controller = controller;
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());

        // top: title + filter options
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Filter & Sort");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(title);
        topPanel.add(Box.createVerticalStrut(10));

        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Position dropdown
        gbc.gridx = 0;
        gbc.gridy = 0;
        filterPanel.add(new JLabel("Position:"), gbc);

        positionComboBox = new JComboBox<>(new String[]{
                "", "G", "F", "C", "PG", "SG", "SF", "PF", "GF", "FC"
        });
        gbc.gridx = 1;
        filterPanel.add(positionComboBox, gbc);

        // Team field
        gbc.gridx = 2;
        filterPanel.add(new JLabel("Team:"), gbc);

        teamField = new JTextField(5);
        gbc.gridx = 3;
        filterPanel.add(teamField, gbc);

        // Season range
        gbc.gridx = 4;
        filterPanel.add(new JLabel("Season from:"), gbc);

        seasonFromField = new JTextField(4);
        gbc.gridx = 5;
        filterPanel.add(seasonFromField, gbc);

        gbc.gridx = 6;
        filterPanel.add(new JLabel("to:"), gbc);

        seasonToField = new JTextField(4);
        gbc.gridx = 7;
        filterPanel.add(seasonToField, gbc);

        // Buttons
        JButton filterButton = new JButton("Filter");
        filterButton.addActionListener(e -> onFilterClicked());

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> onClearClicked());

        JButton homeButton = new JButton("Home");
        homeButton.addActionListener(e -> controller.onHomeButtonClicked());

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        filterPanel.add(filterButton, gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 2;
        filterPanel.add(clearButton, gbc);

        gbc.gridx = 4;
        gbc.gridwidth = 2;
        filterPanel.add(homeButton, gbc);

        topPanel.add(filterPanel);
        add(topPanel, BorderLayout.NORTH);

        // table
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // click header to sort
        JTableHeader header = table.getTableHeader();
        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int columnIndex = header.columnAtPoint(e.getPoint());
                controller.onColumnHeaderClicked(columnIndex);
            }
        });
    }

    private void onFilterClicked() {
        String pos = (String) positionComboBox.getSelectedItem();
        String team = teamField.getText();
        String from = seasonFromField.getText();
        String to = seasonToField.getText();
        controller.onFilterButtonClicked(pos, team, from, to);
    }

    private void onClearClicked() {
        positionComboBox.setSelectedIndex(0);
        teamField.setText("");
        seasonFromField.setText("");
        seasonToField.setText("");
        controller.onClearFilters();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SortState state = viewModel.getState();
        List<String[]> rows = state.getTableData();

        tableModel.setRowCount(0);

        if (rows != null) {
            for (String[] row : rows) {
                Object[] data = new Object[COLUMN_NAMES.length];
                for (int i = 0; i < COLUMN_NAMES.length; i++) {
                    if (row != null && i < row.length) {
                        data[i] = row[i];
                    } else {
                        data[i] = "";
                    }
                }
                tableModel.addRow(data);
            }
        }

        if (state.getErrorMessage() != null) {
            JOptionPane.showMessageDialog(this, state.getErrorMessage());
        }
    }
}
