package view;

import javax.swing.*;
import java.awt.*;
import com.formdev.flatlaf.FlatLaf;

public class LoadingPanel extends JPanel {

    public LoadingPanel() {
        this.setPreferredSize(new Dimension(30, 30)); // Small size for bubble
        this.setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        int size = Math.min(getWidth(), getHeight()) - 8;
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;
        
        // Calculate angle based on system time for smooth animation in renderers
        int angle = (int) ((System.currentTimeMillis() / 5) % 360);

        // Draw track
        g2.setColor(FlatLaf.isLafDark() ? new Color(80, 80, 80) : new Color(200, 200, 200)); // Dynamic grey for track
        g2.drawOval(x, y, size, size);
        
        // Draw spinner
        g2.setColor(new Color(50, 150, 250)); // Blueish
        g2.drawArc(x, y, size, size, -angle, 120); // Negative angle for clockwise
    }
}
