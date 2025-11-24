package view;

import interface_adapter.sort_players.SortController;
import interface_adapter.sort_players.SortState;
import interface_adapter.sort_players.SortViewModel;
import interface_adapter.filter_players.FilterPlayersController;
import interface_adapter.filter_players.FilterPlayersState;
import interface_adapter.filter_players.FilterPlayersViewModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SortPlayersView extends JPanel implements PropertyChangeListener {

    public final String viewName = "sort_players";

    private final SortController sortController;
    private final SortViewModel sortViewModel;

    private final FilterPlayersController filterController;
    private final FilterPlayersViewModel filterViewModel;

    private final JTable table;
    private final DefaultTableModel tableModel;

    private final JComboBox<String> positionComboBox;
    private final JTextField teamField;
    private final JTextField seasonFromField;
    private final JTextField seasonToField;

    private final JLabel bannerLabel;

    // 17 columns, same order as CSV
    private static final String[] COLUMN_NAMES = {
            "Name", "Pos", "Age", "Team", "Season",
            "G", "MP", "FG%", "3P%", "FT%",
            "TRB", "AST", "TOV", "STL", "BLK", "PF", "PTS"
    };

    public SortPlayersView(SortController sortController,
                           SortViewModel sortViewModel,
                           FilterPlayersController filterController,
                           FilterPlayersViewModel filterViewModel) {
        this.sortController = sortController;
        this.sortViewModel = sortViewModel;
        this.filterController = filterController;
        this.filterViewModel = filterViewModel;

        this.sortViewModel.addPropertyChangeListener(this);
        this.filterViewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());

        // top: title + banner + filter options
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Filter & Sort");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(title);
        topPanel.add(Box.createVerticalStrut(10));

        bannerLabel = new JLabel("");
        bannerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bannerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(bannerLabel);
        topPanel.add(Box.createVerticalStrut(5));

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
        homeButton.addActionListener(e -> sortController.onHomeButtonClicked());

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
                sortController.onColumnHeaderClicked(columnIndex);
            }
        });
    }

    private void onFilterClicked() {
        String pos = (String) positionComboBox.getSelectedItem();
        String team = teamField.getText().trim();
        String from = seasonFromField.getText().trim();
        String to = seasonToField.getText().trim();

        Set<String> positions = new HashSet<>();
        if (pos != null && !pos.isEmpty()) {
            positions.add(pos);
        }

        Set<String> teams = new HashSet<>();
        if (!team.isEmpty()) {
            teams.add(team);
        }

        java.util.Optional<Integer> seasonMin = parseSeason(from);
        java.util.Optional<Integer> seasonMax = parseSeason(to);

        filterController.apply(teams, positions, seasonMin, seasonMax);
    }

    private void onClearClicked() {
        positionComboBox.setSelectedIndex(0);
        teamField.setText("");
        seasonFromField.setText("");
        seasonToField.setText("");

        // Clear filter state and table
        filterController.clear();
        // Also let sort use case restore the full list if it has that behavior
        sortController.onClearFilters();
    }

    private java.util.Optional<Integer> parseSeason(String text) {
        if (text == null || text.isEmpty()) {
            return java.util.Optional.empty();
        }
        try {
            return java.util.Optional.of(Integer.parseInt(text));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Season year must be an integer.");
            return java.util.Optional.empty();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!"state".equals(evt.getPropertyName())) {
            return;
        }

        Object source = evt.getSource();

        if (source == sortViewModel) {
            Object newValue = evt.getNewValue();
            if (newValue instanceof SortState) {
                updateFromSortState((SortState) newValue);
            } else {
                updateFromSortState(sortViewModel.getState());
            }
        } else if (source == filterViewModel) {
            FilterPlayersState state = filterViewModel.getState();
            updateFromFilterState(state);
        }
    }

    private void updateFromSortState(SortState state) {
        List<String[]> rows = state.getTableData();
        reloadTableFromRows(rows);

        if (state.getErrorMessage() != null) {
            JOptionPane.showMessageDialog(this, state.getErrorMessage());
        }
    }

    private void updateFromFilterState(FilterPlayersState state) {
        List<String[]> rows = state.tableRows != null
                ? state.tableRows
                : new ArrayList<>();

        reloadTableFromRows(rows);

        if (state.bannerMessage != null) {
            bannerLabel.setText(state.bannerMessage);
        } else {
            bannerLabel.setText("");
        }

        if (state.errorMessage != null && !state.errorMessage.isEmpty()) {
            JOptionPane.showMessageDialog(this, state.errorMessage);
        }
    }

    private void reloadTableFromRows(List<String[]> rows) {
        tableModel.setRowCount(0);

        if (rows == null) {
            return;
        }

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
}
