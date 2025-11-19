package view;

import interface_adapter.favourite.FavouriteController;
import interface_adapter.favourite.FavouriteState;
import interface_adapter.favourite.FavouriteViewModel;
import interface_adapter.search_player.SearchPlayerController;
import interface_adapter.search_player.SearchPlayerViewModel;
import interface_adapter.search_player.SearchPlayerState;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
import use_case.search_player.SearchPlayerOutputBoundary;

/**
 * The SearchPlayerView is the UI where users can search for NBA players and
 * view their selected statistics over a selected season range in a table and graph format.
 */
public class SearchPlayerView extends JPanel implements ActionListener, PropertyChangeListener {

    public final String viewName = "search_player";

    private final SearchPlayerController controller;
    private final SearchPlayerViewModel viewModel;
    private final FavouriteController favouriteController;

    private final JTextField playerNameField;
    private final JTextField startSeasonField;
    private final JTextField endSeasonField;
    private final JCheckBox ppgBox, apgBox, rpgBox, fgBox;
    private final JButton searchButton;
    private final JTable resultTable;
    private final DefaultTableModel tableModel;
    private final JButton homeButton;
    private final JButton clearButton;
    private final JButton favouriteButton;
    private boolean isFavourite = false;
    private String selected = null;
    private final ImageIcon starEmpty;
    private final ImageIcon starFilled;

    private LineChart<Number, Number> lineChart;

    public SearchPlayerView(SearchPlayerController controller,
                            SearchPlayerViewModel viewModel,
                            FavouriteController favouriteController,
                            FavouriteViewModel favouriteViewModel) {
        this.controller = controller;
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);
        favouriteViewModel.addPropertyChangeListener(this);
        this.favouriteController = favouriteController;

        // Load and resize icons
        starEmpty = resizeIcon("/icons/star_empty.png", 30, 30);
        starFilled = resizeIcon("/icons/star_filled.png", 30, 30);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("Search for Player");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        playerNameField = new JTextField();
        startSeasonField = new JTextField();
        endSeasonField = new JTextField();

        playerNameField.setPreferredSize(new Dimension(120, 25));
        startSeasonField.setPreferredSize(new Dimension(80, 25));
        endSeasonField.setPreferredSize(new Dimension(80, 25));

        favouriteButton = new JButton();
        favouriteButton.setPreferredSize(new Dimension(40, 40));
        favouriteButton.setBorderPainted(false);
        favouriteButton.setContentAreaFilled(false);
        favouriteButton.setFocusPainted(false);
        favouriteButton.setIcon(starEmpty);
        favouriteButton.setEnabled(false); // Disabled until search

        favouriteButton.addActionListener(e -> {
            if (selected != null) {
                favouriteController.favouriteToggle(selected);
            }
        });

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Player Name:"), gbc);

        JPanel nameRow = new JPanel(new BorderLayout());
        nameRow.add(playerNameField, BorderLayout.CENTER);
        nameRow.add(favouriteButton, BorderLayout.EAST);

        gbc.gridx = 1;
        inputPanel.add(nameRow, gbc);

        // Removed duplicate addition of favouriteButton at gridx=2

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Start Season:"), gbc);

        gbc.gridx = 1;
        inputPanel.add(startSeasonField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("End Season:"), gbc);

        gbc.gridx = 1;
        inputPanel.add(endSeasonField, gbc);


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

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));

        searchButton = new JButton("Search");
        clearButton = new JButton("Clear");
        homeButton = new JButton("Home");

        searchButton.addActionListener(this);
        clearButton.addActionListener(this);
        homeButton.addActionListener(this);

        buttonRow.add(searchButton);
        buttonRow.add(clearButton);
        buttonRow.add(homeButton);

        String[] columnNames = {"Season", "PPG", "APG", "RPG", "FG%"};
        tableModel = new DefaultTableModel(columnNames, 0);
        resultTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(resultTable);


        JFXPanel fxPanel = new JFXPanel();
        fxPanel.setPreferredSize(new Dimension(600, 300));

        Platform.runLater(() -> {
            NumberAxis xAxis = new NumberAxis();
            xAxis.setAutoRanging(false);
            xAxis.setLowerBound(1980);
            xAxis.setUpperBound(2024);
            xAxis.setTickUnit(1);
            xAxis.setLabel("Season");

            xAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(xAxis) {
                @Override
                public String toString(Number object) {
                    return String.valueOf(object.intValue());
                }
            });

            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Stat Value");

            lineChart = new LineChart<>(xAxis, yAxis);
            lineChart.setTitle("Performance Over Seasons");
            lineChart.setLegendVisible(true);
            lineChart.setLegendSide(javafx.geometry.Side.BOTTOM);
            lineChart.setAnimated(false);

            fxPanel.setScene(new Scene(lineChart, 600, 300));
        });

        add(title);
        add(Box.createRigidArea(new Dimension(0, 15)));
        add(inputPanel);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(statPanel);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(buttonRow);
        add(Box.createRigidArea(new Dimension(0, 15)));
        add(scrollPane);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(fxPanel);
    }

    private ImageIcon resizeIcon(String path, int width, int height) {
        try {
            java.net.URL imgURL = getClass().getResource(path);
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image img = icon.getImage();
                Image newImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(newImg);
            } else {
                System.err.println("Couldn't find file: " + path);
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
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
            
            // Reset selection
            selected = null;
            isFavourite = false;
            favouriteButton.setEnabled(false);
            updateStarIcon();

            Platform.runLater(() -> lineChart.getData().clear());
            return;
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
        if (evt.getNewValue() instanceof FavouriteState) {
            if (selected == null) {
                return;
            }
            FavouriteState fs = (FavouriteState) evt.getNewValue();

            String currentName = selected;
            isFavourite = fs.getFavourites().contains(currentName.toLowerCase());
            System.out.println("Now " + selected + " is isFavourite:" + isFavourite);
            updateStarIcon();
            return;
        }
        else {
            SearchPlayerState state = (SearchPlayerState) evt.getNewValue();

            if (state.getErrorMessage() != null && !state.getErrorMessage().isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        state.getErrorMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            selected = playerNameField.getText().trim();
            isFavourite = favouriteController.isFavourite(selected.toLowerCase());
            favouriteButton.setEnabled(true); // Enable button
            updateStarIcon();
            System.out.println(selected + " " + isFavourite);

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
    private void updateStarIcon() {
        if (isFavourite) {
            favouriteButton.setIcon(starFilled);
        } else {
            favouriteButton.setIcon(starEmpty);
        }
    }
}
