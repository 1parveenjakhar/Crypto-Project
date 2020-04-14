package com.UI;

import javax.swing.*;
import java.awt.*;

public class RoundTextField extends JTextField {
    private final int radius;

    public RoundTextField(int x, int y, int w, int h, int r, Color fColor) {
        radius = r;
        setBounds(x, y, w, h);
        setMargin(new Insets(0, 10, 0, 10));
        setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        setForeground(fColor);
        setOpaque(false);
        setCaretColor(Color.WHITE);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
    protected void paintBorder(Graphics g) {
        g.setColor(Color.WHITE);
        g.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, radius, radius);
    }
}
