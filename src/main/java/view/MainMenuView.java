package view;

import interface_adapter.main_menu.MainMenuController;
import interface_adapter.main_menu.MainMenuViewModel;
import interface_adapter.main_menu.MainMenuState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class MainMenuView extends JPanel implements ActionListener, PropertyChangeListener {
    public final String viewName = "main_menu";
    private final MainMenuViewModel mainMenuViewModel;
    private final MainMenuController mainMenuController;
    private final JButton searchForPlayer;
    private final JButton filterAndSort;
    private final JButton compare;
    private final JButton aiInsights;
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);
    private static final Font BUTTON_FONT = new Font("Arial", Font.PLAIN, 18);


    public MainMenuView(MainMenuViewModel mainMenuViewModel, MainMenuController controller) {
        this.mainMenuController = controller;
        this.mainMenuViewModel = mainMenuViewModel;
        mainMenuViewModel.addPropertyChangeListener(this);

        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(MainMenuViewModel.TITLE_LABEL);
        title.setFont(TITLE_FONT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
        // Ensure all buttons are the same size
        Dimension buttonSize = new Dimension(300, 60); // width, height
        searchForPlayer = new JButton(MainMenuViewModel.SEARCH_FOR_PLAYER_BUTTON_LABEL);
        searchForPlayer.setFont(BUTTON_FONT);
        searchForPlayer.setAlignmentX(Component.CENTER_ALIGNMENT);
        searchForPlayer.setMaximumSize(buttonSize);
        searchForPlayer.setPreferredSize(buttonSize);

        filterAndSort = new JButton(MainMenuViewModel.FILTER_SORT_BUTTON_LABEL);
        filterAndSort.setFont(BUTTON_FONT);
        filterAndSort.setAlignmentX(Component.CENTER_ALIGNMENT);
        filterAndSort.setMaximumSize(buttonSize);
        filterAndSort.setPreferredSize(buttonSize);

        compare = new JButton(MainMenuViewModel.COMPARE_BUTTON_LABEL);
        compare.setFont(BUTTON_FONT);
        compare.setAlignmentX(Component.CENTER_ALIGNMENT);
        compare.setMaximumSize(buttonSize);
        compare.setPreferredSize(buttonSize);

        aiInsights = new JButton(MainMenuViewModel.AI_INSIGHTS_BUTTON_LABEL);
        aiInsights.setFont(BUTTON_FONT);
        aiInsights.setAlignmentX(Component.CENTER_ALIGNMENT);
        aiInsights.setMaximumSize(buttonSize);
        aiInsights.setPreferredSize(buttonSize);

        JButton aiChat = new JButton(MainMenuViewModel.AI_CHAT_BUTTON_LABEL);
        aiChat.setFont(BUTTON_FONT);
        aiChat.setAlignmentX(Component.CENTER_ALIGNMENT);
        aiChat.setMaximumSize(buttonSize);
        aiChat.setPreferredSize(buttonSize);

        buttons.add(searchForPlayer);
        buttons.add(Box.createRigidArea(new Dimension(0, 20)));
        buttons.add(filterAndSort);
        buttons.add(Box.createRigidArea(new Dimension(0, 20)));
        buttons.add(compare);
        buttons.add(Box.createRigidArea(new Dimension(0, 20)));
        buttons.add(aiInsights);
        buttons.add(Box.createRigidArea(new Dimension(0, 20)));
        buttons.add(aiChat);

        searchForPlayer.addActionListener(this);
        aiInsights.addActionListener(this);
        aiChat.addActionListener(this);

        compare.addActionListener(this);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(title);
        this.add(Box.createRigidArea(new Dimension(0, 40)));
        this.add(buttons);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(searchForPlayer)) {
            mainMenuController.onSearchPlayerPressed();
        } else if (e.getSource().equals(aiInsights)) {
            mainMenuController.switchToGenerateInsights();
        } else if (e.getSource() instanceof JButton && ((JButton) e.getSource()).getText().equals(MainMenuViewModel.AI_CHAT_BUTTON_LABEL)) {
            mainMenuController.switchToChat();
        } else if (e.getSource().equals(compare)) {
            mainMenuController.onCompareButtonClicked();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        MainMenuState state = (MainMenuState) evt.getNewValue();
        if (state.getError() != null) {
            JOptionPane.showMessageDialog(this, state.getError());
        }
    }
}
