package view;

import com.formdev.flatlaf.FlatClientProperties;
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
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

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
    private final JButton clearButton;
    private final JButton homeButton;
    private final JToggleButton favouriteButton; // Changed to ToggleButton
    private boolean isFavourite = false;
    private String selected = null;

    private final JTable resultTable;
    private final DefaultTableModel tableModel;
    private LineChart<Number, Number> lineChart;
    private final JPanel searchFormPanel;
    private final JFXPanel fxPanel;

    public SearchPlayerView(SearchPlayerController controller,
                            SearchPlayerViewModel viewModel,
                            FavouriteController favouriteController,
                            FavouriteViewModel favouriteViewModel) {
        this.controller = controller;
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);
        favouriteViewModel.addPropertyChangeListener(this);
        this.favouriteController = favouriteController;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- Header ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Player Search & Analysis");
        title.putClientProperty(FlatClientProperties.STYLE, "font: bold +8");
        homeButton = new JButton("Home");
        homeButton.putClientProperty(FlatClientProperties.STYLE, "buttonType: roundRect");
        headerPanel.add(homeButton, BorderLayout.WEST);
        headerPanel.add(title, BorderLayout.CENTER);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);

        // --- Main Content (Scrollable if needed, but we'll try to fit) ---
        JPanel mainContent = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.weightx = 1.0;

        // 1. Search Form Panel
        searchFormPanel = new JPanel(new GridBagLayout());
        searchFormPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor"), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        searchFormPanel.putClientProperty(FlatClientProperties.STYLE, "arc: 15; background: lighten($Panel.background, 3%)");

        GridBagConstraints fgbc = new GridBagConstraints();
        fgbc.fill = GridBagConstraints.HORIZONTAL;
        fgbc.insets = new Insets(5, 5, 5, 5);
        
        // Row 1: Player Name
        fgbc.gridx = 0; fgbc.gridy = 0;
        searchFormPanel.add(new JLabel("Player Name:"), fgbc);
        
        fgbc.gridx = 1; fgbc.weightx = 1.0;
        playerNameField = new JTextField();
        playerNameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "e.g. LeBron James");
        searchFormPanel.add(playerNameField, fgbc);
        
        fgbc.gridx = 2; fgbc.weightx = 0;
        favouriteButton = new JToggleButton("★");
        favouriteButton.setToolTipText("Toggle Favourite");
        favouriteButton.putClientProperty(FlatClientProperties.STYLE, "font: bold +4; buttonType: roundRect");
        favouriteButton.setEnabled(false);
        searchFormPanel.add(favouriteButton, fgbc);

        // Row 2: Season Range
        fgbc.gridx = 0; fgbc.gridy = 1; fgbc.weightx = 0;
        searchFormPanel.add(new JLabel("Season Range:"), fgbc);
        
        JPanel seasonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        startSeasonField = new JTextField(6);
        startSeasonField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Start");
        endSeasonField = new JTextField(6);
        endSeasonField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "End");
        seasonPanel.add(startSeasonField);
        seasonPanel.add(new JLabel("  to  "));
        seasonPanel.add(endSeasonField);
        
        fgbc.gridx = 1; fgbc.gridwidth = 2;
        searchFormPanel.add(seasonPanel, fgbc);
        
        // Row 3: Stats Checkboxes
        fgbc.gridx = 0; fgbc.gridy = 2; fgbc.gridwidth = 3;
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ppgBox = new JCheckBox("PPG");
        apgBox = new JCheckBox("APG");
        rpgBox = new JCheckBox("RPG");
        fgBox  = new JCheckBox("FG%");
        statsPanel.add(new JLabel("Stats: "));
        statsPanel.add(ppgBox);
        statsPanel.add(apgBox);
        statsPanel.add(rpgBox);
        statsPanel.add(fgBox);
        searchFormPanel.add(statsPanel, fgbc);

        // Row 4: Action Buttons
        fgbc.gridy = 3;
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchButton = new JButton("Search");
        searchButton.putClientProperty(FlatClientProperties.STYLE, "font: bold; type: default");
        clearButton = new JButton("Clear");
        
        actionPanel.add(clearButton);
        actionPanel.add(searchButton);
        searchFormPanel.add(actionPanel, fgbc);

        // Add Search Panel to Main Content
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weighty = 0;
        mainContent.add(searchFormPanel, gbc);

        // 2. Results Table
        String[] columnNames = {"Season", "PPG", "APG", "RPG", "FG%"};
        tableModel = new DefaultTableModel(columnNames, 0) {
             @Override
             public boolean isCellEditable(int row, int column) {
                 return false;
             }
        };
        resultTable = new JTable(tableModel);
        resultTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setPreferredSize(new Dimension(0, 150));
        
        gbc.gridy = 1;
        gbc.weighty = 0.3;
        mainContent.add(scrollPane, gbc);

        // 3. Chart
        fxPanel = new JFXPanel();
        fxPanel.setBorder(BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor")));
        
        gbc.gridy = 2;
        gbc.weighty = 0.7;
        mainContent.add(fxPanel, gbc);

        add(mainContent, BorderLayout.CENTER);

        // Listeners
        searchButton.addActionListener(this);
        clearButton.addActionListener(this);
        homeButton.addActionListener(this);
        favouriteButton.addActionListener(e -> {
            if (selected != null) {
                favouriteController.favouriteToggle(selected);
            }
        });

        // Initialize Chart
        Platform.runLater(() -> {
            NumberAxis xAxis = new NumberAxis();
            xAxis.setAutoRanging(false);
            xAxis.setLowerBound(1980);
            xAxis.setUpperBound(2024);
            xAxis.setTickUnit(1);
            xAxis.setLabel("Season");
            xAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(xAxis) {
                @Override public String toString(Number object) { return String.valueOf(object.intValue()); }
            });

            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Stat Value");

            lineChart = new LineChart<>(xAxis, yAxis);
            lineChart.setTitle("Performance History");
            lineChart.setLegendVisible(true);
            lineChart.setLegendSide(Side.LEFT);
            lineChart.setAnimated(false);
            
            // Minimal CSS for dark mode chart if needed, but default might suffice or use stylesheets
            // lineChart.getStylesheets().add("..."); 

            fxPanel.setScene(new Scene(lineChart));
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == homeButton) {
            viewModel.getViewManagerModel().setActiveView("main_menu");
            viewModel.getViewManagerModel().firePropertyChanged();
        } else if (e.getSource() == clearButton) {
            clearFields();
        } else if (e.getSource() == searchButton) {
            performSearch();
        }
    }
    
    private void clearFields() {
        playerNameField.setText("");
        startSeasonField.setText("");
        endSeasonField.setText("");
        ppgBox.setSelected(false);
        apgBox.setSelected(false);
        rpgBox.setSelected(false);
        fgBox.setSelected(false);
        tableModel.setRowCount(0);
        selected = null;
        isFavourite = false;
        favouriteButton.setEnabled(false);
        favouriteButton.setSelected(false);
        Platform.runLater(() -> lineChart.getData().clear());
    }

    private void performSearch() {
        String playerName  = playerNameField.getText().trim();
        String startSeason = startSeasonField.getText().trim();
        String endSeason   = endSeasonField.getText().trim();

        List<String> selectedStats = new ArrayList<>();
        if (ppgBox.isSelected()) selectedStats.add("PPG");
        if (apgBox.isSelected()) selectedStats.add("APG");
        if (rpgBox.isSelected()) selectedStats.add("RPG");
        if (fgBox.isSelected())  selectedStats.add("FG");

        controller.executeSearch(playerName, startSeason, endSeason, selectedStats);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getNewValue() instanceof FavouriteState) {
            if (selected == null) return;
            FavouriteState fs = (FavouriteState) evt.getNewValue();
            isFavourite = fs.getFavourites().contains(selected.toLowerCase());
            favouriteButton.setSelected(isFavourite);
        } else {
            SearchPlayerState state = (SearchPlayerState) evt.getNewValue();
            if (state.getErrorMessage() != null && !state.getErrorMessage().isEmpty()) {
                JOptionPane.showMessageDialog(this, state.getErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            selected = playerNameField.getText().trim();
            isFavourite = favouriteController.isFavourite(selected.toLowerCase());
            favouriteButton.setEnabled(true);
            favouriteButton.setSelected(isFavourite);

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
                        series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                    }
                    lineChart.getData().add(series);
                }
            });
        }
    }
    
    public void setPlayerNameForSearch(String playerName) {
        if (playerName == null) return;
        playerNameField.setText(playerName);
        selected = playerName;
        isFavourite = favouriteController.isFavourite(selected.toLowerCase());
        favouriteButton.setEnabled(true);
        favouriteButton.setSelected(isFavourite);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (searchFormPanel != null) {
             searchFormPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor"), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            ));
        }
        if (fxPanel != null) {
            fxPanel.setBorder(BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor")));
        }
    }
}
