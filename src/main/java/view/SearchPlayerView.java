package view;

import interface_adapter.search_player.SearchPlayerController;
import interface_adapter.search_player.SearchPlayerViewModel;
import interface_adapter.search_player.SearchPlayerState;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.embed.swing.JFXPanel;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

/**
 * The SearchPlayerView is the UI where users can search for NBA players and
 * view their selected statistics over a selected season range in a table and graph format.
 */
public class SearchPlayerView extends JPanel implements ActionListener, PropertyChangeListener {

    public final String viewName = "search_player";

    private final SearchPlayerController controller;
    private final SearchPlayerViewModel viewModel;

    private final JTextField playerNameField;
    private final JTextField startSeasonField;
    private final JTextField endSeasonField;
    private final JCheckBox ppgBox, apgBox, rpgBox, fgBox;
    private final JButton searchButton;
    private final JTable resultTable;
    private final DefaultTableModel tableModel;
    private final JButton homeButton;
    private final JButton clearButton;

    private LineChart<Number, Number> lineChart;

    public SearchPlayerView(SearchPlayerController controller,
                            SearchPlayerViewModel viewModel) {
        this.controller = controller;
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Search for Player");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        playerNameField = new JTextField();
        startSeasonField = new JTextField();
        endSeasonField = new JTextField();

        inputPanel.add(new JLabel("Player Name:"));
        inputPanel.add(playerNameField);
        inputPanel.add(new JLabel("Start Season:"));
        inputPanel.add(startSeasonField);
        inputPanel.add(new JLabel("End Season:"));
        inputPanel.add(endSeasonField);

        // ---- Stat selection panel ----
        JPanel statPanel = new JPanel(new FlowLayout());
        statPanel.add(new JLabel("Select Stats:"));

        ppgBox = new JCheckBox("Points Per Game");
        apgBox = new JCheckBox("Assists Per Game");
        rpgBox = new JCheckBox("Rebounds Per Game");
        fgBox  = new JCheckBox("Field Goal Percentage");

        statPanel.add(ppgBox);
        statPanel.add(apgBox);
        statPanel.add(rpgBox);
        statPanel.add(fgBox);

        searchButton = new JButton("Search");
        searchButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        searchButton.addActionListener(this);

        homeButton = new JButton("Home");
        homeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        clearButton = new JButton("Clear");
        clearButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        homeButton.addActionListener(this);
        clearButton.addActionListener(this);

        String[] columnNames = {"Season", "PPG", "APG", "RPG", "FG%"};
        tableModel = new DefaultTableModel(columnNames, 0);
        resultTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(resultTable);

        add(Box.createRigidArea(new Dimension(0, 10)));
        add(title);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(inputPanel);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(statPanel);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(searchButton);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(homeButton);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(clearButton);
        add(Box.createRigidArea(new Dimension(0, 15)));
        add(scrollPane);

        JFXPanel fxPanel = new JFXPanel();
        fxPanel.setPreferredSize(new Dimension(600, 300));
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(fxPanel);

        Platform.runLater(() -> {
            NumberAxis xAxis = new NumberAxis();
            xAxis.setAutoRanging(false);
            xAxis.setLowerBound(1980);
            xAxis.setUpperBound(2024);
            xAxis.setTickUnit(1);
            NumberAxis yAxis = new NumberAxis();
            xAxis.setLabel("Season");
            yAxis.setLabel("Stat Value");

            lineChart = new LineChart<>(xAxis, yAxis);
            lineChart.setTitle("Performance Over Seasons");

            fxPanel.setScene(new Scene(lineChart, 600, 300));
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == homeButton) {
            viewModel.getViewManagerModel().setActiveView("main_menu");
            viewModel.getViewManagerModel().firePropertyChanged();
        }

        if (e.getSource() == clearButton) {
            playerNameField.setText("");
            startSeasonField.setText("");
            endSeasonField.setText("");

            ppgBox.setSelected(false);
            apgBox.setSelected(false);
            rpgBox.setSelected(false);
            fgBox.setSelected(false);

            tableModel.setRowCount(0);

            Platform.runLater(() -> lineChart.getData().clear());
        }

        if (e.getSource() == searchButton) {
            String playerName  = playerNameField.getText().trim();
            String startSeason = startSeasonField.getText().trim();
            String endSeason   = endSeasonField.getText().trim();

            List<String> selectedStats = new ArrayList<>();
            if (ppgBox.isSelected()) {
                selectedStats.add("PPG");
            }
            if (apgBox.isSelected()) {
                selectedStats.add("APG");
            }
            if (rpgBox.isSelected()) {
                selectedStats.add("RPG");
            }
            if (fgBox.isSelected()) {
                selectedStats.add("FG");
            }

            controller.executeSearch(playerName, startSeason, endSeason, selectedStats);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SearchPlayerState state = (SearchPlayerState) evt.getNewValue();

        // Handle error from presenter/interactor
        if (state.getErrorMessage() != null && !state.getErrorMessage().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    state.getErrorMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        tableModel.setRowCount(0);
        for (String[] row : state.getResultsTableData()) {
            tableModel.addRow(row);
        }

        Platform.runLater(() -> {
            lineChart.getData().clear();

            Map<String, Map<Integer, Double>> graphData = state.getGraphData();
            if (graphData == null) return;

            for (String statName : graphData.keySet()) {
                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                series.setName(statName);

                for (var entry : graphData.get(statName).entrySet()) {
                    int season = entry.getKey();
                    double value = entry.getValue();
                    series.getData().add(new XYChart.Data<>(season, value));
                }

                lineChart.getData().add(series);
            }
        });
    }
}
