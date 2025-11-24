package view;

import interface_adapter.filter_players.FilterPlayersController;
import interface_adapter.filter_players.FilterPlayersState;
import interface_adapter.filter_players.FilterPlayersViewModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.stream.Collectors;

public class FilterPlayersView extends JPanel {
    public final String viewName = "filter players";
    private final FilterPlayersViewModel vm;
    private final FilterPlayersController controller;

    private final JPanel badges = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private final JTable table = new JTable();
    private final JLabel pageInfoLabel = new JLabel();

    // Fixed position options required by your spec
    private static final String[] POSITION_OPTIONS = {
            "PG", "G", "SG", "GF", "F", "SF", "PF", "FC", "C"
    };

    public FilterPlayersView(FilterPlayersViewModel vm, FilterPlayersController controller) {
        this.vm = vm;
        this.controller = controller;
        setLayout(new BorderLayout(12, 12));

        // ===== LEFT FILTER PANEL =====
        JPanel filters = new JPanel();
        filters.setLayout(new BoxLayout(filters, BoxLayout.Y_AXIS));

        // --- Teams dropdown (3-letter abbreviations from CSV) ---
        java.util.List<String> teamList = new ArrayList<>(vm.getAllTeams());
        Collections.sort(teamList);
        JComboBox<String> teamCombo = new JComboBox<>();
        teamCombo.addItem("Any team");
        for (String t : teamList) {
            teamCombo.addItem(t);
        }
        filters.add(new JLabel("Team"));
        filters.add(teamCombo);
        filters.add(Box.createVerticalStrut(10));

        // --- Positions dropdown (fixed list) ---
        JComboBox<String> posCombo = new JComboBox<>();
        posCombo.addItem("Any position");
        for (String p : POSITION_OPTIONS) {
            posCombo.addItem(p);
        }
        filters.add(new JLabel("Position"));
        filters.add(posCombo);
        filters.add(Box.createVerticalStrut(10));

        // --- Season From / To dropdowns ---
        final int MIN_SEASON = 1980;
        final int MAX_SEASON = 2025;

        JComboBox<String> seasonFromCombo = new JComboBox<>();
        JComboBox<String> seasonToCombo = new JComboBox<>();

        seasonFromCombo.addItem("Any");
        seasonToCombo.addItem("Any");

        for (int year = MIN_SEASON; year <= MAX_SEASON; year++) {
            String y = String.valueOf(year);
            seasonFromCombo.addItem(y);
            seasonToCombo.addItem(y);
        }

        filters.add(new JLabel("Season From"));
        filters.add(seasonFromCombo);
        filters.add(Box.createVerticalStrut(5));
        filters.add(new JLabel("Season To"));
        filters.add(seasonToCombo);
        filters.add(Box.createVerticalStrut(10));

        // --- Apply / Clear buttons ---
        JButton apply = new JButton("Apply Filters");
        JButton clear = new JButton("Clear Filters");
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonRow.add(apply);
        buttonRow.add(clear);
        filters.add(Box.createVerticalStrut(10));
        filters.add(buttonRow);

        // ===== RIGHT SIDE: BADGES + TABLE + PAGINATION =====
        JPanel right = new JPanel(new BorderLayout(8, 8));
        right.add(badges, BorderLayout.NORTH);

        table.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Name", "Team", "Pos", "Seasons"}
        ));
        right.add(new JScrollPane(table), BorderLayout.CENTER);

        // Pagination controls
        JButton prevPage = new JButton("Previous");
        JButton nextPage = new JButton("Next");
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        paginationPanel.add(prevPage);
        paginationPanel.add(nextPage);
        paginationPanel.add(pageInfoLabel);
        right.add(paginationPanel, BorderLayout.SOUTH);

        add(filters, BorderLayout.WEST);
        add(right, BorderLayout.CENTER);

        // ===== VIEW-MODEL LISTENER =====
        vm.addPropertyChangeListener(evt -> {
            FilterPlayersState state = vm.getState();

            // Update table rows
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);
            for (String[] row : state.tableRows) {
                model.addRow(row);
            }

            // Update badges
            badges.removeAll();
            if (!state.selectedTeams.isEmpty()) {
                badges.add(new JLabel("Teams: " + String.join(", ", state.selectedTeams)));
            }
            if (!state.selectedPositions.isEmpty()) {
                badges.add(new JLabel("Positions: " + String.join(", ", state.selectedPositions)));
            }
            if (state.seasonMin.isPresent() || state.seasonMax.isPresent()) {
                String fromLabel = state.seasonMin.map(String::valueOf).orElse("Any");
                String toLabel = state.seasonMax.map(String::valueOf).orElse("Any");
                badges.add(new JLabel("Seasons: " + fromLabel + "–" + toLabel));
            }
            badges.revalidate();
            badges.repaint();

            // Update page info / banner
            pageInfoLabel.setText(state.bannerMessage != null ? state.bannerMessage : "");
        });

        // ===== APPLY BUTTON LOGIC =====
        apply.addActionListener(e -> {
            // Team selection
            Set<String> teams = new HashSet<>();
            String selectedTeam = (String) teamCombo.getSelectedItem();
            if (selectedTeam != null && !"Any team".equals(selectedTeam)) {
                teams.add(selectedTeam);
            }

            // Position selection (uppercased to match CSV)
            Set<String> positions = new HashSet<>();
            String selectedPos = (String) posCombo.getSelectedItem();
            if (selectedPos != null && !"Any position".equals(selectedPos)) {
                positions.add(selectedPos.toUpperCase());
            }

            // Season From / To
            Optional<Integer> sMin = Optional.empty();
            Optional<Integer> sMax = Optional.empty();

            String fromVal = (String) seasonFromCombo.getSelectedItem();
            String toVal = (String) seasonToCombo.getSelectedItem();

            if (fromVal != null && !"Any".equals(fromVal)) {
                sMin = Optional.of(Integer.parseInt(fromVal));
            }
            if (toVal != null && !"Any".equals(toVal)) {
                sMax = Optional.of(Integer.parseInt(toVal));
            }

            // Update VM state so badges reflect selections
            FilterPlayersState state = vm.getState();
            state.selectedTeams = teams;
            state.selectedPositions = positions;
            state.seasonMin = sMin;
            state.seasonMax = sMax;
            state.currentPage = 0; // reset to first page on new filters

            controller.apply(teams, positions, sMin, sMax);
        });

        // ===== CLEAR BUTTON LOGIC =====
        clear.addActionListener(e -> {
            teamCombo.setSelectedIndex(0);
            posCombo.setSelectedIndex(0);
            seasonFromCombo.setSelectedIndex(0);
            seasonToCombo.setSelectedIndex(0);
            controller.clear();
        });

        // ===== PAGINATION BUTTONS =====
        prevPage.addActionListener(e -> {
            FilterPlayersState state = vm.getState();
            if (state.allRows.isEmpty()) return;

            if (state.currentPage > 0) {
                state.currentPage--;
                updatePageFromStateAndNotify();
            }
        });

        nextPage.addActionListener(e -> {
            FilterPlayersState state = vm.getState();
            if (state.allRows.isEmpty()) return;

            int total = state.allRows.size();
            int pageSize = state.pageSize <= 0 ? 50 : state.pageSize;
            int maxPage = (total - 1) / pageSize;

            if (state.currentPage < maxPage) {
                state.currentPage++;
                updatePageFromStateAndNotify();
            }
        });

        // Load initial data so the table is NOT empty when you first open the screen
        controller.clear();
    }

    /** Local helper used by the pagination buttons. */
    private void updatePageFromStateAndNotify() {
        FilterPlayersState state = vm.getState();
        int total = state.allRows.size();
        int pageSize = state.pageSize <= 0 ? 50 : state.pageSize;
        int maxPage = (total == 0) ? 0 : (total - 1) / pageSize;

        if (state.currentPage < 0) state.currentPage = 0;
        if (state.currentPage > maxPage) state.currentPage = maxPage;

        if (total == 0) {
            state.tableRows = java.util.List.of();
            state.bannerMessage = "No players match your filters.";
        } else {
            int from = state.currentPage * pageSize;
            int to = Math.min(total, from + pageSize);
            state.tableRows = state.allRows.subList(from, to);

            int humanPage = state.currentPage + 1;
            int totalPages = maxPage + 1;
            state.bannerMessage = "Showing " + (from + 1) + "-" + to + " of "
                    + total + " players (page " + humanPage + " of " + totalPages + ").";
        }

        vm.firePropertyChanged();
    }
}
