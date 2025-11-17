package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.RoundRectangle2D;

public class ChatMessagePanel extends JPanel {
    private final JTextArea messageArea;
    private final ChatMessage.Sender sender;
    private static final int BUBBLE_RADIUS = 25;
    private static final Color USER_BUBBLE_COLOR = new Color(0, 122, 255); // Blue
    private static final Color AI_BUBBLE_COLOR = new Color(229, 229, 234); // Light Gray
    private static final Font MESSAGE_FONT = new Font("Arial", Font.PLAIN, 16);

    public ChatMessagePanel(ChatMessage message) {
        this.sender = message.getSender();
        setOpaque(false); // Make the panel transparent

        messageArea = new JTextArea(message.getMessage());
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setOpaque(false); // Make text area transparent
        messageArea.setFont(MESSAGE_FONT);
        messageArea.setMargin(new Insets(12, 12, 12, 12)); // Padding inside the bubble

        if (sender == ChatMessage.Sender.USER) {
            messageArea.setForeground(Color.WHITE);
        } else {
            messageArea.setForeground(Color.BLACK);
        }

        // Use a JPanel to hold the messageArea and act as the bubble
        JPanel bubbleContent = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();

                RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(0, 0, width - 1, height - 1, BUBBLE_RADIUS, BUBBLE_RADIUS);

                if (sender == ChatMessage.Sender.USER) {
                    g2.setColor(USER_BUBBLE_COLOR);
                } else {
                    g2.setColor(AI_BUBBLE_COLOR);
                }
                g2.fill(roundedRectangle);
            }
        };
        bubbleContent.setOpaque(false); // Make bubble content panel transparent
        bubbleContent.add(messageArea, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(bubbleContent, BorderLayout.CENTER);
    }

    @Override
    public Dimension getPreferredSize() {
        // Create a temporary dummy component to calculate the preferred size
        JTextArea dummy = new JTextArea(messageArea.getText());
        dummy.setLineWrap(true);
        dummy.setWrapStyleWord(true);
        dummy.setFont(MESSAGE_FONT);
        dummy.setMargin(new Insets(12, 12, 12, 12));

        // Determine the maximum width for the bubble
        int maxWidth;
        if (getParent() != null && getParent().getWidth() > 0) {
            maxWidth = (int) (getParent().getWidth() * 0.6);
        } else {
            // Fallback to a reasonable default if the parent is not yet realized
            maxWidth = 300;
        }

        // Set the size of the dummy text area to calculate the wrapped height
        dummy.setSize(new Dimension(maxWidth, Short.MAX_VALUE));
        Dimension preferredSize = dummy.getPreferredSize();

        // Add padding for the bubble shape
        preferredSize.width = Math.min(preferredSize.width, maxWidth) + 24;
        preferredSize.height += 24;

        return preferredSize;
    }
}
