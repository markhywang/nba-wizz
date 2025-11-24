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
import java.util.*;
import java.util.List;

public class FilterSortPlayersView extends JPanel implements PropertyChangeListener {

    public final String viewName = "sort_players";

    private final SortController sortController;
    private final SortViewModel sortViewModel;

    private final FilterPlayersController filterController;
    private final FilterPlayersViewModel filterViewModel;

    private final JTable table;
    private final DefaultTableModel tableModel;

    private final JComboBox<String> positionComboBox;
    private final JComboBox<String> teamComboBox;
    private final JComboBox<String> seasonFromComboBox;
    private final JComboBox<String> seasonToComboBox;

    private final JLabel bannerLabel;

    // 17 columns, same order as CSV
    private static final String[] COLUMN_NAMES = {
            "Name", "Pos", "Age", "Team", "Season",
            "G", "MP", "FG%", "3P%", "FT%",
            "TRB", "AST", "TOV", "STL", "BLK", "PF", "PTS"
    };

    public FilterSortPlayersView(SortController sortController,
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

        // ---------- Top panel ----------
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

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

        // Position
        gbc.gridx = 0;
        gbc.gridy = 0;
        filterPanel.add(new JLabel("Position:"), gbc);

        positionComboBox = new JComboBox<>(new String[]{
                "", "G", "F", "C", "PG", "SG", "SF", "PF", "GF", "FC"
        });
        gbc.gridx = 1;
        filterPanel.add(positionComboBox, gbc);

        // Team (editable combo)
        gbc.gridx = 2;
        filterPanel.add(new JLabel("Team:"), gbc);

        String[] teamOptions = buildTeamOptionsSafe();
        teamComboBox = new JComboBox<>(teamOptions);
        teamComboBox.setEditable(true);
        gbc.gridx = 3;
        filterPanel.add(teamComboBox, gbc);

        // Season range (editable combos)
        gbc.gridx = 4;
        filterPanel.add(new JLabel("Season from:"), gbc);

        String[] seasonOptions = buildSeasonOptions();
        seasonFromComboBox = new JComboBox<>(seasonOptions);
        seasonFromComboBox.setEditable(true);
        gbc.gridx = 5;
        filterPanel.add(seasonFromComboBox, gbc);

        gbc.gridx = 6;
        filterPanel.add(new JLabel("to:"), gbc);

        seasonToComboBox = new JComboBox<>(seasonOptions);
        seasonToComboBox.setEditable(true);
        gbc.gridx = 7;
        filterPanel.add(seasonToComboBox, gbc);

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

        // ---------- Table ----------
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JScrollPane scrollPane = new JScrollPane(
                table,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        JTableHeader header = table.getTableHeader();
        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int columnIndex = header.columnAtPoint(e.getPoint());
                sortController.onColumnHeaderClicked(columnIndex);
            }
        });
    }

    // Team options: safe against null viewModel / null set
    private String[] buildTeamOptionsSafe() {
        Set<String> allTeams = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        if (filterViewModel != null && filterViewModel.getAllTeams() != null) {
            allTeams.addAll(filterViewModel.getAllTeams());
        }
        List<String> list = new ArrayList<>();
        list.add(""); // empty option
        list.addAll(allTeams);
        return list.toArray(new String[0]);
    }

    // Simple year list; can adjust range later
    private String[] buildSeasonOptions() {
        int minYear = 1980;
        int maxYear = 2024;
        List<String> list = new ArrayList<>();
        list.add("");
        for (int y = maxYear; y >= minYear; y--) {
            list.add(String.valueOf(y));
        }
        return list.toArray(new String[0]);
    }

    private void onFilterClicked() {
        String pos = (String) positionComboBox.getSelectedItem();

        Object teamItem = teamComboBox.getEditor().getItem();
        String team = teamItem == null ? "" : teamItem.toString().trim();

        Object fromItem = seasonFromComboBox.getEditor().getItem();
        String from = fromItem == null ? "" : fromItem.toString().trim();

        Object toItem = seasonToComboBox.getEditor().getItem();
        String to = toItem == null ? "" : toItem.toString().trim();

        Set<String> positions = new HashSet<>();
        if (pos != null && !pos.isEmpty()) {
            positions.add(pos);
        }

        Set<String> teams = new HashSet<>();
        if (!team.isEmpty()) {
            teams.add(team);
        }

        Optional<Integer> seasonMin = parseSeason(from);
        Optional<Integer> seasonMax = parseSeason(to);

        filterController.apply(teams, positions, seasonMin, seasonMax);
    }

    private void onClearClicked() {
        positionComboBox.setSelectedIndex(0);
        teamComboBox.setSelectedItem("");
        seasonFromComboBox.setSelectedItem("");
        seasonToComboBox.setSelectedItem("");

        filterController.clear();
    }

    private Optional<Integer> parseSeason(String text) {
        if (text == null || text.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(Integer.parseInt(text));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Season year must be an integer.");
            return Optional.empty();
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
        List<String[]> rows;
        if (state.tableRows != null) {
            rows = state.tableRows;
        } else {
            rows = new ArrayList<>();
        }

        reloadTableFromRows(rows);

        SortState sortState = sortViewModel.getState();
        sortState.setTableData(rows);

        if (sortState.getOriginalTableData() == null ||
                sortState.getOriginalTableData().isEmpty()) {
            sortState.setOriginalTableData(new ArrayList<>(rows));
        }

        sortState.setSortedColumnIndex(-1);
        sortState.setAscending(true);
        sortState.setErrorMessage(state.errorMessage);

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
