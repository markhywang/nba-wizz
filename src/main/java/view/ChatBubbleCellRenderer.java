package view;

import javax.swing.*;
import java.awt.*;

public class ChatBubbleCellRenderer implements ListCellRenderer<ChatMessage> {
    @Override
    public Component getListCellRendererComponent(JList<? extends ChatMessage> list, ChatMessage value, int index, boolean isSelected, boolean cellHasFocus) {
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setOpaque(false); // Make wrapper panel transparent
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Add vertical spacing and horizontal padding

        ChatMessagePanel chatMessagePanel = new ChatMessagePanel(value, list.getWidth());

        if (value.getSender() == ChatMessage.Sender.USER) {
            wrapperPanel.add(chatMessagePanel, BorderLayout.EAST);
        } else if (value.getSender() == ChatMessage.Sender.LOADING) {
            wrapperPanel.add(new LoadingPanel(), BorderLayout.WEST);
        } else {
            wrapperPanel.add(chatMessagePanel, BorderLayout.WEST);
        }

        // Set background for selection if needed, though bubbles will handle their own colors
        if (isSelected) {
            wrapperPanel.setBackground(list.getSelectionBackground());
        } else {
            wrapperPanel.setBackground(list.getBackground());
        }

        return wrapperPanel;
    }
}

