package view;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import com.formdev.flatlaf.FlatLaf;

public class ChatMessagePanel extends JPanel {
    private final JEditorPane messagePane;
    private final ChatMessage.Sender sender;
    private static final int BUBBLE_RADIUS = 20;
    private static final Color USER_BUBBLE_COLOR = new Color(37, 99, 235); // Modern Blue
    
    private static final Parser parser = Parser.builder().build();
    private static final HtmlRenderer renderer = HtmlRenderer.builder().build();

    private final int parentWidth;

    public ChatMessagePanel(ChatMessage message, int parentWidth) {
        this.sender = message.getSender();
        this.parentWidth = parentWidth;
        setOpaque(false);

        Node document = parser.parse(message.getMessage().trim());
        String htmlContent = renderer.render(document);

        // Text color logic
        String fontColor;
        if (sender == ChatMessage.Sender.USER) {
            fontColor = "white";
        } else {
            fontColor = FlatLaf.isLafDark() ? "white" : "black";
        }
        
        String css = "body { font-family: 'Segoe UI', sans-serif; font-size: 14px; color: " + fontColor + "; margin: 0; } p { margin-top: 0; margin-bottom: 5px; } code { background-color: #444; padding: 2px; border-radius: 4px; }";
        
        messagePane = new JEditorPane();
        messagePane.setEditorKit(new HTMLEditorKit());
        messagePane.setEditable(false);
        messagePane.setOpaque(false);
        messagePane.setMargin(new Insets(12, 12, 12, 12));
        
        HTMLEditorKit kit = (HTMLEditorKit) messagePane.getEditorKit();
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule(css);
        
        messagePane.setText("<html><body>" + htmlContent + "</body></html>");

        JPanel bubbleContent = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();

                RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(0, 0, width, height, BUBBLE_RADIUS, BUBBLE_RADIUS);

                if (sender == ChatMessage.Sender.USER) {
                    g2.setColor(USER_BUBBLE_COLOR);
                } else {
                    g2.setColor(FlatLaf.isLafDark() ? new Color(60, 60, 65) : new Color(230, 230, 235));
                }
                g2.fill(roundedRectangle);
                g2.dispose();
            }
        };
        bubbleContent.setOpaque(false);
        bubbleContent.add(messagePane, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(bubbleContent, BorderLayout.CENTER);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (messagePane != null) {
            HTMLEditorKit kit = (HTMLEditorKit) messagePane.getEditorKit();
            StyleSheet styleSheet = kit.getStyleSheet();
            String fontColor;
            if (sender == ChatMessage.Sender.USER) {
                fontColor = "white";
            } else {
                fontColor = FlatLaf.isLafDark() ? "white" : "black";
            }
            styleSheet.addRule("body { color: " + fontColor + "; }");
        }
    }

    @Override
    public Dimension getPreferredSize() {
        int maxWidth;
        if (parentWidth > 0) {
            maxWidth = (int) (parentWidth * 0.70);
        } else {
            maxWidth = 400;
        }

        // Force width to calculate height
        messagePane.setSize(new Dimension(maxWidth, Integer.MAX_VALUE));
        Dimension pref = messagePane.getPreferredSize();
        // Add padding to preferred size for the bubble borders
        return new Dimension(Math.min(pref.width, maxWidth), pref.height);
    }
}
