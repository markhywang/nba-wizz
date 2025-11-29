package view;

import com.formdev.flatlaf.FlatClientProperties;
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

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Player Database");
        title.putClientProperty(FlatClientProperties.STYLE, "font: bold +8");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        
        JButton homeButton = new JButton("Home");
        homeButton.putClientProperty(FlatClientProperties.STYLE, "buttonType: roundRect");
        homeButton.addActionListener(e -> sortController.onHomeButtonClicked());
        
        JPanel titleWrapper = new JPanel(new BorderLayout());
        titleWrapper.add(homeButton, BorderLayout.WEST);
        titleWrapper.add(title, BorderLayout.CENTER);
        titleWrapper.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        bannerLabel = new JLabel(" ");
        bannerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bannerLabel.setForeground(new Color(48, 209, 88));
        
        headerPanel.add(titleWrapper, BorderLayout.NORTH);
        headerPanel.add(bannerLabel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Filter Panel
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 20, 10, 20),
            BorderFactory.createTitledBorder("Filters")
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1
        gbc.gridx = 0; gbc.gridy = 0;
        filterPanel.add(new JLabel("Position:"), gbc);
        positionComboBox = new JComboBox<>(new String[]{"", "G", "F", "C", "PG", "SG", "SF", "PF", "GF", "FC"});
        gbc.gridx = 1;
        filterPanel.add(positionComboBox, gbc);

        gbc.gridx = 2;
        filterPanel.add(new JLabel("Team:"), gbc);
        teamComboBox = new JComboBox<>(buildTeamOptionsSafe());
        teamComboBox.setEditable(true);
        gbc.gridx = 3;
        filterPanel.add(teamComboBox, gbc);

        gbc.gridx = 4;
        filterPanel.add(new JLabel("Season:"), gbc);
        
        JPanel seasonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        seasonFromComboBox = new JComboBox<>(buildSeasonOptions());
        seasonFromComboBox.setEditable(true);
        seasonToComboBox = new JComboBox<>(buildSeasonOptions());
        seasonToComboBox.setEditable(true);
        seasonPanel.add(seasonFromComboBox);
        seasonPanel.add(new JLabel(" - "));
        seasonPanel.add(seasonToComboBox);
        gbc.gridx = 5;
        filterPanel.add(seasonPanel, gbc);

        // Row 2: Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton clearButton = new JButton("Clear");
        JButton filterButton = new JButton("Apply Filter");
        filterButton.putClientProperty(FlatClientProperties.STYLE, "font: bold; type: default");
        
        clearButton.addActionListener(e -> onClearClicked());
        filterButton.addActionListener(e -> onFilterClicked());
        
        actionPanel.add(clearButton);
        actionPanel.add(filterButton);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 6; gbc.anchor = GridBagConstraints.EAST;
        filterPanel.add(actionPanel, gbc);

        add(filterPanel, BorderLayout.CENTER);

        // Table
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getTableHeader().setReorderingAllowed(false);
        
        // Enhance table scrolling
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        // Container to hold filter + table
        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.add(filterPanel, BorderLayout.NORTH);
        centerContainer.add(scrollPane, BorderLayout.CENTER);
        
        add(centerContainer, BorderLayout.CENTER);

        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int columnIndex = table.getTableHeader().columnAtPoint(e.getPoint());
                sortController.onColumnHeaderClicked(columnIndex);
            }
        });
    }

    private String[] buildTeamOptionsSafe() {
        Set<String> allTeams = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        if (filterViewModel != null && filterViewModel.getAllTeams() != null) {
            allTeams.addAll(filterViewModel.getAllTeams());
        }
        List<String> list = new ArrayList<>();
        list.add("");
        list.addAll(allTeams);
        return list.toArray(new String[0]);
    }

    private String[] buildSeasonOptions() {
        List<String> list = new ArrayList<>();
        list.add("");
        for (int y = 2024; y >= 1980; y--) list.add(String.valueOf(y));
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
        if (pos != null && !pos.isEmpty()) positions.add(pos);

        Set<String> teams = new HashSet<>();
        if (!team.isEmpty()) teams.add(team);

        Optional<Integer> seasonMin = parseSeason(from);
        Optional<Integer> seasonMax = parseSeason(to);

        if (seasonMin.isPresent() && seasonMax.isPresent() && seasonMin.get() > seasonMax.get()) {
            JOptionPane.showMessageDialog(this, "Start season cannot be greater than end season.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

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
        if (text == null || text.isEmpty()) return Optional.empty();
        try {
            return Optional.of(Integer.parseInt(text));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Season year must be an integer.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return Optional.empty();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!"state".equals(evt.getPropertyName())) return;
        Object source = evt.getSource();

        if (source == sortViewModel) {
            updateFromSortState(sortViewModel.getState());
        } else if (source == filterViewModel) {
            updateFromFilterState(filterViewModel.getState());
        }
    }

    private void updateFromSortState(SortState state) {
        reloadTableFromRows(state.getTableData());
        if (state.getErrorMessage() != null) {
            JOptionPane.showMessageDialog(this, state.getErrorMessage());
        }
    }

    private void updateFromFilterState(FilterPlayersState state) {
        List<String[]> rows = state.tableRows != null ? state.tableRows : new ArrayList<>();
        reloadTableFromRows(rows);

        SortState sortState = sortViewModel.getState();
        sortState.setTableData(rows);
        if (sortState.getOriginalTableData() == null || sortState.getOriginalTableData().isEmpty()) {
            sortState.setOriginalTableData(new ArrayList<>(rows));
        }
        sortState.setSortedColumnIndex(-1);
        sortState.setAscending(true);
        sortState.setErrorMessage(state.errorMessage);

        bannerLabel.setText(state.bannerMessage != null ? state.bannerMessage : "");
        if (state.errorMessage != null && !state.errorMessage.isEmpty()) {
            JOptionPane.showMessageDialog(this, state.errorMessage);
        }
    }

    private void reloadTableFromRows(List<String[]> rows) {
        tableModel.setRowCount(0);
        if (rows == null) return;
        for (String[] row : rows) {
            Object[] data = new Object[COLUMN_NAMES.length];
            for (int i = 0; i < COLUMN_NAMES.length; i++) {
                data[i] = (row != null && i < row.length) ? row[i] : "";
            }
            tableModel.addRow(data);
        }
    }
}
