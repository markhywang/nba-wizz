package view;

import interface_adapter.filter_players.FilterPlayersController;
import interface_adapter.filter_players.FilterPlayersViewModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class FilterPlayersView extends JPanel {
    public final String viewName = "filter players";
    private final FilterPlayersViewModel vm;
    private final FilterPlayersController controller;

    private final JPanel badges = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private final JTable table = new JTable();

    public FilterPlayersView(FilterPlayersViewModel vm, FilterPlayersController controller) {
        this.vm = vm;
        this.controller = controller;
        setLayout(new BorderLayout(12,12));

        // Left filter panel
        JPanel filters = new JPanel();
        filters.setLayout(new BoxLayout(filters, BoxLayout.Y_AXIS));

        // Team multi-select list
        JList<String> teamList = new JList<>(vm.getAllTeams().toArray(new String[0]));
        teamList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane teamScroll = new JScrollPane(teamList);
        teamScroll.setPreferredSize(new Dimension(160, 140));
        filters.add(new JLabel("Teams"));
        filters.add(teamScroll);

        // Position multi-select list
        JList<String> posList = new JList<>(vm.getAllPositions().toArray(new String[0]));
        posList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane posScroll = new JScrollPane(posList);
        posScroll.setPreferredSize(new Dimension(160, 120));
        filters.add(Box.createVerticalStrut(8));
        filters.add(new JLabel("Positions"));
        filters.add(posScroll);

        // Season range slider (min..max inferred from data you load into VM)
        int seasonMin = 1979; // fallback defaults; adjust if you have a known range
        int seasonMax = 2025;
        JSlider seasonSlider = new JSlider(JSlider.HORIZONTAL, seasonMin, seasonMax, seasonMin);
        seasonSlider.setPaintTicks(true);
        seasonSlider.setPaintLabels(true);
        seasonSlider.setMajorTickSpacing(5);
        seasonSlider.setMinorTickSpacing(1);
        // Use two sliders for min/max to keep it simple:
        JSlider seasonMaxSlider = new JSlider(JSlider.HORIZONTAL, seasonMin, seasonMax, seasonMax);
        seasonMaxSlider.setPaintTicks(true);
        seasonMaxSlider.setPaintLabels(true);
        seasonMaxSlider.setMajorTickSpacing(5);
        seasonMaxSlider.setMinorTickSpacing(1);

        filters.add(Box.createVerticalStrut(8));
        filters.add(new JLabel("Season Min"));
        filters.add(seasonSlider);
        filters.add(new JLabel("Season Max"));
        filters.add(seasonMaxSlider);

        // Buttons
        JButton apply = new JButton("Apply Filters");
        JButton clear = new JButton("Clear Filters");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(apply);
        buttons.add(clear);
        filters.add(Box.createVerticalStrut(10));
        filters.add(buttons);

        // Right: badges + table
        JPanel right = new JPanel(new BorderLayout(8,8));
        right.add(badges, BorderLayout.NORTH);
        right.add(new JScrollPane(table), BorderLayout.CENTER);

        add(filters, BorderLayout.WEST);
        add(right, BorderLayout.CENTER);

        // Table model
        table.setModel(new DefaultTableModel(new Object[][]{}, new String[]{"Name","Team","Pos","Seasons"}));

        // VM listener
        vm.addPropertyChangeListener(evt -> {
            // banner message (warning/notice/error)
            String msg = vm.getState().bannerMessage;
            if (msg != null && !msg.isBlank()) {
                // non-blocking: show in a label panel above table
                right.add(new JLabel(msg), BorderLayout.SOUTH);
                right.revalidate();
            }
            // rows
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);
            for (String[] row : vm.getState().tableRows) model.addRow(row);

            // badges
            badges.removeAll();
            if (!vm.getState().selectedTeams.isEmpty()) {
                badges.add(new JLabel("Teams: " + String.join(", ", vm.getState().selectedTeams)));
            }
            if (!vm.getState().selectedPositions.isEmpty()) {
                badges.add(new JLabel("Positions: " + String.join(", ", vm.getState().selectedPositions)));
            }
            if (vm.getState().seasonMin.isPresent() || vm.getState().seasonMax.isPresent()) {
                badges.add(new JLabel("Seasons: " +
                        vm.getState().seasonMin.orElse(seasonMin) + "–" +
                        vm.getState().seasonMax.orElse(seasonMax)));
            }
            badges.revalidate();
            badges.repaint();
        });

        // Actions
        apply.addActionListener(e -> {
            Set<String> teams = teamList.getSelectedValuesList().stream().collect(Collectors.toSet());
            Set<String> positions = posList.getSelectedValuesList().stream().collect(Collectors.toSet());
            Optional<Integer> sMin = Optional.of(seasonSlider.getValue());
            Optional<Integer> sMax = Optional.of(seasonMaxSlider.getValue());

            // Update VM state (so badges reflect selections)
            vm.getState().selectedTeams = teams;
            vm.getState().selectedPositions = positions;
            vm.getState().seasonMin = sMin;
            vm.getState().seasonMax = sMax;

            controller.apply(teams, positions, sMin, sMax);
        });

        clear.addActionListener(e -> controller.clear());
    }
}