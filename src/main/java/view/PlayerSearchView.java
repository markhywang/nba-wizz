package view;

import interface_adapter.player_search.PlayerSearchController;
import interface_adapter.player_search.PlayerSearchViewModel;
import interface_adapter.player_search.PlayerSearchState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class PlayerSearchView extends JPanel implements ActionListener, PropertyChangeListener {
    public final String viewName = "player_search";
    private final PlayerSearchViewModel playerSearchViewModel;
    private final PlayerSearchController playerSearchController;
    private final JButton searchForPlayer;
    private final JButton filterAndSort;
    private final JButton compare;
    private final JButton aiInsights;

    public PlayerSearchView(PlayerSearchViewModel playerSearchViewModel, PlayerSearchController controller) {
        this.playerSearchController = controller;
        this.playerSearchViewModel = playerSearchViewModel;
        playerSearchViewModel.addPropertyChangeListener(this);

        JLabel title = new JLabel(PlayerSearchViewModel.TITLE_LABEL);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
        // Ensure all buttons are the same size
        Dimension buttonSize = new Dimension(220, 40); // width, height
        searchForPlayer = new JButton(PlayerSearchViewModel.SEARCH_FOR_PLAYER_BUTTON_LABEL);
        searchForPlayer.setAlignmentX(Component.CENTER_ALIGNMENT);
        searchForPlayer.setMaximumSize(buttonSize);
        searchForPlayer.setPreferredSize(buttonSize);

        filterAndSort = new JButton(PlayerSearchViewModel.FILTER_SORT_BUTTON_LABEL);
        filterAndSort.setAlignmentX(Component.CENTER_ALIGNMENT);
        filterAndSort.setMaximumSize(buttonSize);
        filterAndSort.setPreferredSize(buttonSize);

        compare = new JButton(PlayerSearchViewModel.COMPARE_BUTTON_LABEL);
        compare.setAlignmentX(Component.CENTER_ALIGNMENT);
        compare.setMaximumSize(buttonSize);
        compare.setPreferredSize(buttonSize);

        aiInsights = new JButton(PlayerSearchViewModel.AI_INSIGHTS_BUTTON_LABEL);
        aiInsights.setAlignmentX(Component.CENTER_ALIGNMENT);
        aiInsights.setMaximumSize(buttonSize);
        aiInsights.setPreferredSize(buttonSize);

        buttons.add(searchForPlayer);
        buttons.add(Box.createRigidArea(new Dimension(0, 10)));
        buttons.add(filterAndSort);
        buttons.add(Box.createRigidArea(new Dimension(0, 10)));
        buttons.add(compare);
        buttons.add(Box.createRigidArea(new Dimension(0, 10)));
        buttons.add(aiInsights);

        searchForPlayer.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        if (evt.getSource().equals(searchForPlayer)) {
                            // TODO: Implement search for player functionality
                        }
                    }
                }
        );
        // TODO: Add action listeners for other buttons

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(title);
        this.add(Box.createRigidArea(new Dimension(0, 20)));
        this.add(buttons);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // This is for future use, if other actions are added.
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        PlayerSearchState state = (PlayerSearchState) evt.getNewValue();
        if (state.getError() != null) {
            JOptionPane.showMessageDialog(this, state.getError());
        }
    }
}
