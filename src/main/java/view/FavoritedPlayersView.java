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

        JLabel titleLabel = new JLabel("Favorited Players", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(listPanel);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            viewManagerModel.setActiveView("main_menu");
            viewManagerModel.firePropertyChanged();
        });
        bottom.add(backButton);
        add(bottom, BorderLayout.SOUTH);

        // initialize list from view model state
        updateListFromState();
    }

    private void updateListFromState() {
        listPanel.removeAll();
        var state = favouriteViewModel.getState();
        if (state != null && state.getFavourites() != null && !state.getFavourites().isEmpty()) {
            for (String p : state.getFavourites()) {
                JPanel row = new JPanel(new BorderLayout());
                JLabel nameLabel = new JLabel(p);
                nameLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                row.add(nameLabel, BorderLayout.CENTER);

                JButton searchBtn = new JButton("Search");
                searchBtn.addActionListener(e -> {
                    // Prefill the search view's player name so the UI (star etc.) updates correctly
                    searchPlayerView.setPlayerNameForSearch(p);
                    // switch to search view and run a search for this player with default seasons
                    viewManagerModel.setActiveView("search_player");
                    viewManagerModel.firePropertyChanged();
                    // perform search with a default valid season range so interactor accepts it
                    searchPlayerController.executeSearch(p, "1980", "2024", new java.util.ArrayList<>());
                });
                row.add(searchBtn, BorderLayout.EAST);

                listPanel.add(row);
                listPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        } else {
            JLabel empty = new JLabel("No favourited players yet.", SwingConstants.CENTER);
            empty.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
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