package view;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ChatMessagePanel extends JPanel {
    private final JEditorPane messagePane;
    private final ChatMessage.Sender sender;
    private static final int BUBBLE_RADIUS = 25;
    private static final Color USER_BUBBLE_COLOR = new Color(0, 122, 255); // Blue
    private static final Color AI_BUBBLE_COLOR = new Color(229, 229, 234); // Light Gray
    private static final Font MESSAGE_FONT = new Font("Arial", Font.PLAIN, 16);

    private static final Parser parser = Parser.builder().build();
    private static final HtmlRenderer renderer = HtmlRenderer.builder().build();

    private final int parentWidth;

    public ChatMessagePanel(ChatMessage message, int parentWidth) {
        this.sender = message.getSender();
        this.parentWidth = parentWidth;
        setOpaque(false); // Make the panel transparent

        // Convert Markdown to HTML
        Node document = parser.parse(message.getMessage().trim());
        String htmlContent = renderer.render(document);

        // Style the HTML
        String fontColor = (sender == ChatMessage.Sender.USER) ? "white" : "black";
        // Adjusted font size to 12px (pt vs px conversion roughly 16px ~ 12pt, but HTMLEditorKit interprets px/pt. Let's try 14px or 'medium')
        // Actually, let's use CSS font-size: 14px for readability.
        String css = "body { font-family: Arial, sans-serif; font-size: 14px; color: " + fontColor + "; margin: 0; } p { margin-top: 0; margin-bottom: 5px; }";
        
        // Use JEditorPane
        messagePane = new JEditorPane();
        messagePane.setEditorKit(new HTMLEditorKit());
        messagePane.setEditable(false);
        messagePane.setOpaque(false);
        messagePane.setMargin(new Insets(10, 10, 10, 10));
        
        // Apply CSS
        HTMLEditorKit kit = (HTMLEditorKit) messagePane.getEditorKit();
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule(css);
        
        messagePane.setText("<html><body>" + htmlContent + "</body></html>");


        // Use a JPanel to hold the messagePane and act as the bubble
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
        bubbleContent.add(messagePane, BorderLayout.CENTER);

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

        // Force the width to trigger correct height calculation for wrapped text
        messagePane.setSize(new Dimension(maxWidth, Integer.MAX_VALUE));
        Dimension pref = messagePane.getPreferredSize();
        
        return new Dimension(Math.min(pref.width, maxWidth), pref.height);
    }
}
