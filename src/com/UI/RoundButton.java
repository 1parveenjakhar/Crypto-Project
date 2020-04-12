package com.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class RoundButton extends JButton {
    private final int radius;
    private final Color backColor;
    private final boolean borderPaint;

    public RoundButton(String label, int x, int y, int w, int h, int r, Color fColor, Color bColor, boolean border) {
        super(label);

        radius = r;
        backColor = bColor;
        borderPaint = border;
        setBounds(x, y, w, h);
        setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        setForeground(fColor);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setHorizontalAlignment(SwingConstants.CENTER);
        setMargin(new Insets(0, 0, 0, 0));
        RoundButton tempButton = this;
        this.addMouseListener(new MouseAdapter() {
            Color color = tempButton.getForeground();
            public void mouseEntered(MouseEvent me) {
                color = tempButton.getForeground();
                tempButton.setForeground(Color.LIGHT_GRAY); // change the color to green when mouse over a button
            }
            public void mouseExited(MouseEvent me) {
                tempButton.setForeground(color);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        int buttonWidth = getWidth() - 1;
        int buttonHeight = getHeight() - 1;
        Shape shape = new RoundRectangle2D.Float(0, 0, buttonWidth, buttonHeight, radius, radius);
        g2.setColor(backColor);
        g2.fill(shape);
        setBorderPainted(false);
        g2.dispose();
        super.paintComponent(g);
    }

    protected void paintBorder(Graphics g) {
        if (borderPaint) {
            g.setColor(Color.WHITE);
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        }
    }
}
