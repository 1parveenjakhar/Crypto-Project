package com.UI;

import javax.swing.*;
import java.awt.*;

public class BackgroundPanel extends JPanel {
    /**
     * BackgroundPanel to occupy whole screen
     */
    protected BackgroundPanel() {
        // Remove all restrictions on layout
        this.setLayout(null);
        this.setBounds(0, 0, 1200, 600); // Fill whole page
    }

    // Create and render a gradient
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        int w = getWidth();
        int h = getHeight();
        Color color1 = Color.decode("#013070");
        Color color2 = Color.decode("#5AC9F6");
        GradientPaint gp = new GradientPaint(0, 0, color1, w, h, color2);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, w, h);
    }
}
