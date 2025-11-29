package view;

import interface_adapter.favourite.FavouriteViewModel;
import interface_adapter.favourite.FavouriteController;
import interface_adapter.ViewManagerModel;
import interface_adapter.search_player.SearchPlayerController;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * A panel to display the list of favorited players. This is added to the main CardLayout
 * (so it replaces the main window view when activated).
 */
public class FavoritedPlayersView extends JPanel implements PropertyChangeListener {
    private final FavouriteViewModel favouriteViewModel;
    private final FavouriteController favouriteController;
    private final ViewManagerModel viewManagerModel;
    private final SearchPlayerController searchPlayerController;
    private final SearchPlayerView searchPlayerView;

    private final JPanel listPanel = new JPanel();

    public final String viewName = "favorited_players";

    public FavoritedPlayersView(FavouriteViewModel favouriteViewModel,
                                FavouriteController favouriteController,
                                ViewManagerModel viewManagerModel,
                                SearchPlayerController searchPlayerController,
                                SearchPlayerView searchPlayerView) {

        this.favouriteViewModel = favouriteViewModel;
        this.favouriteController = favouriteController;
        this.viewManagerModel = viewManagerModel;
        this.searchPlayerController = searchPlayerController;
        this.searchPlayerView = searchPlayerView;

        this.favouriteViewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- TITLE ---
        JLabel titleLabel = new JLabel("Favorited Players", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // --- LIST AREA ---
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50)); // centers content
        listPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // --- BOTTOM BUTTONS ---
        JPanel bottom = new JPanel();
        bottom.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        backButton.setPreferredSize(new Dimension(100, 35));
        backButton.addActionListener(e -> {
            viewManagerModel.setActiveView("main_menu");
            viewManagerModel.firePropertyChanged();
        });
        bottom.add(backButton);
        add(bottom, BorderLayout.SOUTH);

        updateListFromState();
    }

    private void updateListFromState() {
        listPanel.removeAll();
        var state = favouriteViewModel.getState();

        if (state != null && state.getFavourites() != null && !state.getFavourites().isEmpty()) {
            for (String p : state.getFavourites()) {

                JPanel row = new JPanel(new BorderLayout());
                row.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(5, 0, 5, 0),
                        BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true)
                ));
                row.setBackground(Color.WHITE);
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

                JLabel nameLabel = new JLabel(p);
                nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                nameLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
                row.add(nameLabel, BorderLayout.WEST);

                JButton searchBtn = new JButton("Search");
                searchBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                searchBtn.setPreferredSize(new Dimension(90, 30));
                searchBtn.addActionListener(e -> {
                    searchPlayerView.setPlayerNameForSearch(p);
                    viewManagerModel.setActiveView("search_player");
                    viewManagerModel.firePropertyChanged();
                    searchPlayerController.executeSearch(p, "1980", "2024", new java.util.ArrayList<>());
                });
                row.add(searchBtn, BorderLayout.EAST);

                listPanel.add(row);
                listPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        } else {
            JLabel empty = new JLabel("No favorited players yet.", SwingConstants.CENTER);
            empty.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            empty.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));
            listPanel.add(empty);
        }

        revalidate();
        repaint();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(this::updateListFromState);
    }
}
