package view;

import interface_adapter.favourite.FavouriteViewModel;
import interface_adapter.favourite.FavouriteController;
import interface_adapter.ViewManagerModel;
import interface_adapter.search_player.SearchPlayerController;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * A panel to display the list of favorited players. This is added to the main CardLayout
 * (so it replaces the main window view when activated).
 */
public class FavouritedPlayersView extends JPanel implements PropertyChangeListener {
    private final FavouriteViewModel favouriteViewModel;
    private final FavouriteController favouriteController;
    private final ViewManagerModel viewManagerModel;
    private final SearchPlayerController searchPlayerController;
    private final SearchPlayerView searchPlayerView;

    private final JPanel listPanel = new JPanel();
    private final ImageIcon starFilled;

    public final String viewName = "favorited_players";

    public FavouritedPlayersView(FavouriteViewModel favouriteViewModel,
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

    // Header
    JLabel titleLabel = new JLabel("Favourited Players", SwingConstants.CENTER);
    titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
    titleLabel.setBorder(new EmptyBorder(12, 8, 12, 8));
    add(titleLabel, BorderLayout.NORTH);

    listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
    listPanel.setBorder(new EmptyBorder(10,10,10,10));
    JScrollPane scrollPane = new JScrollPane(listPanel);
    scrollPane.setBorder(null);
    scrollPane.getViewport().setBackground(Color.WHITE);
    setBackground(Color.WHITE);

    // Load icons used in rows
    starFilled = resizeIcon("/icons/star_filled.png", 20, 20);
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
                // Card-like row
                JPanel row = new JPanel(new BorderLayout());
                row.setBackground(Color.WHITE);
                row.setBorder(new CompoundBorder(new LineBorder(new Color(220,220,220), 1, true), new EmptyBorder(8,10,8,10)));

                JLabel nameLabel = new JLabel(p);
                nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
                nameLabel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
                row.add(nameLabel, BorderLayout.WEST);

                JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
                actions.setOpaque(false);

                // Search button - prominent
                JButton searchBtn = new JButton("Search");
                searchBtn.setFocusable(false);
                searchBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                searchBtn.addActionListener(e -> {
                    searchPlayerView.setPlayerNameForSearch(p);
                    viewManagerModel.setActiveView("search_player");
                    viewManagerModel.firePropertyChanged();
                    searchPlayerController.executeSearch(p, "1980", "2024", new java.util.ArrayList<>());
                });

                // Unfavourite/star button (icon)
                JButton starBtn = new JButton();
                starBtn.setPreferredSize(new Dimension(30, 30));
                starBtn.setBorderPainted(false);
                starBtn.setContentAreaFilled(false);
                starBtn.setFocusPainted(false);
                starBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                if (starFilled != null) starBtn.setIcon(starFilled);
                starBtn.setToolTipText("Remove from favourites");
                starBtn.addActionListener(e -> {
                    favouriteController.favouriteToggle(p);
                });

                // small hover effect for the row
                row.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        row.setBackground(new Color(250,250,250));
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        row.setBackground(Color.WHITE);
                    }
                });

                actions.add(searchBtn);
                actions.add(starBtn);
                row.add(actions, BorderLayout.EAST);

                listPanel.add(row);
                listPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            }
        } else {
            JPanel empty = new JPanel(new BorderLayout());
            empty.setOpaque(false);
            JLabel msg = new JLabel("No favourited players yet.", SwingConstants.CENTER);
            msg.setFont(new Font("SansSerif", Font.PLAIN, 16));
            msg.setBorder(new EmptyBorder(30,10,30,10));
            empty.add(msg, BorderLayout.CENTER);
            listPanel.add(empty);
        }

        revalidate();
        repaint();
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
    public void propertyChange(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(this::updateListFromState);
    }
}