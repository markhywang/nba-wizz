package view;

import com.formdev.flatlaf.FlatClientProperties;
import interface_adapter.main_menu.MainMenuController;
import interface_adapter.main_menu.MainMenuState;
import interface_adapter.main_menu.MainMenuViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class MainMenuView extends JPanel implements ActionListener, PropertyChangeListener {
    public final String viewName = "main_menu";
    private final MainMenuController mainMenuController;
    private final MainMenuViewModel mainMenuViewModel;
    
    private final JButton searchForPlayer;
    private final JButton filterAndSort;
    private final JButton compare;
    private final JButton aiInsights;
    private final JButton viewFavoritedPlayers;
    private final JButton aiChat;

    public MainMenuView(MainMenuViewModel mainMenuViewModel, MainMenuController controller) {
        this.mainMenuViewModel = mainMenuViewModel;
        this.mainMenuController = controller;
        mainMenuViewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));

        JLabel title = new JLabel(MainMenuViewModel.TITLE_LABEL);
        title.putClientProperty(FlatClientProperties.STYLE, "font: bold +24");
        title.setForeground(new Color(37, 99, 235)); // Modern Blue
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitle = new JLabel("Your Ultimate Basketball Companion");
        subtitle.putClientProperty(FlatClientProperties.STYLE, "font: +2; foreground: $Label.disabledForeground");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(title);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitle);
        
        add(headerPanel, BorderLayout.NORTH);

        // Dashboard Grid
        JPanel dashboard = new JPanel(new GridBagLayout());
        dashboard.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        // Initialize buttons
        searchForPlayer = createDashboardButton(MainMenuViewModel.SEARCH_FOR_PLAYER_BUTTON_LABEL, "Search for specific players by name");
        filterAndSort = createDashboardButton(MainMenuViewModel.FILTER_SORT_BUTTON_LABEL, "Filter and sort players by stats");
        compare = createDashboardButton(MainMenuViewModel.COMPARE_BUTTON_LABEL, "Compare two players head-to-head");
        aiInsights = createDashboardButton(MainMenuViewModel.AI_INSIGHTS_BUTTON_LABEL, "Get AI-powered analysis");
        viewFavoritedPlayers = createDashboardButton("View Favorites", "Access your saved players list");
        aiChat = createDashboardButton(MainMenuViewModel.AI_CHAT_BUTTON_LABEL, "Chat with our NBA expert AI");

        // Row 1
        gbc.gridx = 0; gbc.gridy = 0;
        dashboard.add(searchForPlayer, gbc);
        
        gbc.gridx = 1;
        dashboard.add(filterAndSort, gbc);

        // Row 2
        gbc.gridx = 0; gbc.gridy = 1;
        dashboard.add(compare, gbc);

        gbc.gridx = 1;
        dashboard.add(aiInsights, gbc);

        // Row 3
        gbc.gridx = 0; gbc.gridy = 2;
        dashboard.add(viewFavoritedPlayers, gbc);

        gbc.gridx = 1;
        dashboard.add(aiChat, gbc);

        add(dashboard, BorderLayout.CENTER);

        // Listeners
        searchForPlayer.addActionListener(this);
        filterAndSort.addActionListener(this);
        aiInsights.addActionListener(this);
        aiChat.addActionListener(this);
        compare.addActionListener(this);
        viewFavoritedPlayers.addActionListener(this);
    }
    
    private JButton createDashboardButton(String title, String description) {
        JButton button = new JButton("<html><center><span style='font-size:16px; font-weight:bold'>" + title + "</span><br/><br/><span style='font-size:10px'>" + description + "</span></center></html>");
        button.putClientProperty(FlatClientProperties.STYLE, "arc: 15; margin: 10,10,10,10");
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(searchForPlayer)) {
            mainMenuController.onSearchPlayerPressed();
        } else if (e.getSource().equals(filterAndSort)) {
            mainMenuController.onFilterAndSortButtonClicked();
        } else if (e.getSource().equals(aiInsights)) {
            mainMenuController.switchToGenerateInsights();
        } else if (e.getSource().equals(aiChat)) {
            mainMenuController.switchToChat();
        } else if (e.getSource().equals(compare)) {
            mainMenuController.onCompareButtonClicked();
        } else if (e.getSource().equals(viewFavoritedPlayers)) {
            mainMenuController.onViewFavoritedPlayersPressed();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        MainMenuState state = (MainMenuState) evt.getNewValue();
        if (state.getError() != null) {
            JOptionPane.showMessageDialog(this, state.getError(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
