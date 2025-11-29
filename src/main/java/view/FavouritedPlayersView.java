package view;

import com.formdev.flatlaf.FlatClientProperties;
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
        titleLabel.putClientProperty(FlatClientProperties.STYLE, "font: bold +6");
        titleLabel.setBorder(new EmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // List Panel
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // Footer
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        JButton backButton = new JButton("Home");
        backButton.putClientProperty(FlatClientProperties.STYLE, "buttonType: roundRect");
        backButton.addActionListener(e -> {
            viewManagerModel.setActiveView("main_menu");
            viewManagerModel.firePropertyChanged();
        });
        bottom.add(backButton);
        add(bottom, BorderLayout.SOUTH);

        starFilled = resizeIcon("/icons/star_filled.png", 20, 20);
        
        updateListFromState();
    }

    private void updateListFromState() {
        listPanel.removeAll();
        var state = favouriteViewModel.getState();
        if (state != null && state.getFavourites() != null && !state.getFavourites().isEmpty()) {
            for (String p : state.getFavourites()) {
                JPanel row = new JPanel(new BorderLayout());
                // Dark mode friendly border and background
                row.setBorder(new CompoundBorder(
                    new LineBorder(UIManager.getColor("Component.borderColor"), 1, true),
                    new EmptyBorder(10, 15, 10, 15)
                ));
                // Allow FlatLaf to handle background, but give it a slight tint/roundness
                row.putClientProperty(FlatClientProperties.STYLE, "arc: 10; background: lighten($Panel.background, 3%)");

                JLabel nameLabel = new JLabel(p);
                nameLabel.putClientProperty(FlatClientProperties.STYLE, "font: +2");
                row.add(nameLabel, BorderLayout.WEST);

                JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
                actions.setOpaque(false);

                JButton searchBtn = new JButton("Analyze");
                searchBtn.setToolTipText("View Stats");
                searchBtn.putClientProperty(FlatClientProperties.STYLE, "buttonType: roundRect");
                searchBtn.addActionListener(e -> {
                    searchPlayerView.setPlayerNameForSearch(p);
                    viewManagerModel.setActiveView("search_player");
                    viewManagerModel.firePropertyChanged();
                    searchPlayerController.executeSearch(p, "1980", "2024", new java.util.ArrayList<>());
                });

                JButton starBtn = new JButton();
                starBtn.setPreferredSize(new Dimension(32, 32));
                starBtn.putClientProperty(FlatClientProperties.STYLE, "buttonType: roundRect");
                starBtn.setBorderPainted(false);
                starBtn.setContentAreaFilled(false);
                if (starFilled != null) starBtn.setIcon(starFilled);
                starBtn.setToolTipText("Remove from favourites");
                starBtn.addActionListener(e -> {
                    favouriteController.favouriteToggle(p);
                });

                actions.add(searchBtn);
                actions.add(starBtn);
                row.add(actions, BorderLayout.EAST);

                listPanel.add(row);
                listPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        } else {
            JPanel empty = new JPanel(new BorderLayout());
            empty.setOpaque(false);
            JLabel msg = new JLabel("No favourited players yet.", SwingConstants.CENTER);
            msg.putClientProperty(FlatClientProperties.STYLE, "font: +2; foreground: $Label.disabledForeground");
            msg.setBorder(new EmptyBorder(50, 10, 50, 10));
            empty.add(msg, BorderLayout.CENTER);
            listPanel.add(empty);
        }

        listPanel.revalidate();
        listPanel.repaint();
    }
    
    @Override
    public void updateUI() {
        super.updateUI();
        // Rebuild list to update borders if data exists
        if (favouriteViewModel != null) {
            updateListFromState();
        }
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
                return null;
            }
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(this::updateListFromState);
    }
}