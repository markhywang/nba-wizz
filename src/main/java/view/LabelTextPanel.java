package view;

import javax.swing.text.JTextComponent;
import javax.swing.*;
import java.awt.*;

class LabelTextPanel extends JPanel {
    LabelTextPanel(JLabel label, JComponent component) {
        this.add(label);
        this.add(component);
    }
}
