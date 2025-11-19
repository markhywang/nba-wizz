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

    private final int parentWidth;

    public ChatMessagePanel(ChatMessage message, int parentWidth) {
        this.sender = message.getSender();
        this.parentWidth = parentWidth;
        setOpaque(false); // Make the panel transparent

        messageArea = new JTextArea(message.getMessage().trim()); // Trim to remove trailing whitespace
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setOpaque(false); // Make text area transparent
        messageArea.setFont(MESSAGE_FONT);
        messageArea.setMargin(new Insets(10, 10, 10, 10)); // Padding inside the bubble

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
        // Determine the maximum width for the bubble
        int maxWidth;
        if (parentWidth > 0) {
            maxWidth = (int) (parentWidth * 0.65);
        } else {
            maxWidth = 350; // Fallback
        }

        // Calculate the size needed for the text
        JTextArea dummy = new JTextArea(messageArea.getText());
        dummy.setLineWrap(true);
        dummy.setWrapStyleWord(true);
        dummy.setFont(MESSAGE_FONT);
        dummy.setMargin(new Insets(10, 10, 10, 10));
        
        // Force the width to trigger correct height calculation for wrapped text
        dummy.setSize(new Dimension(maxWidth, Short.MAX_VALUE)); 
        
        Dimension preferredSize = dummy.getPreferredSize();
        
        // Return constrained width and calculated height
        // Adding a small buffer to height can sometimes help if borders are cut off, but explicit margin covers it.
        return new Dimension(Math.min(preferredSize.width, maxWidth), preferredSize.height);
    }
}
