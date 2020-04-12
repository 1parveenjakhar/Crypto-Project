package com.UI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.utility.CommonConstants.*;


public class MainFrame extends JFrame {
    public MainFrame() {
        setSize(frameWidth, frameHeight);
        setUndecorated(true);
        Shape shape = new RoundRectangle2D.Float(0, 0, frameWidth, frameHeight, 25, 25);
        setShape(shape);


        // Fix size and center on screen
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
        mainFrame = this;

        RoundButton minimize = new RoundButton("_", frameWidth - 115, 5, 50, 20, 10, Color.white, blueColor, false);
        RoundButton close = new RoundButton("X", frameWidth - 55, 5, 50, 20, 10, Color.white, Color.red, false);
        close.setFont(new Font("ARIAL", Font.BOLD, 13));
        minimize.setFont(new Font("ARIAL", Font.BOLD, 15));
        // Fix cross platform rendering issues. Displays on Windows only.
        minimize.setMargin(new Insets(0, 0, 7, 0));
        close.addActionListener(e -> System.exit(0));
        minimize.addActionListener(e -> setState(JFrame.ICONIFIED));

        add(minimize);
        add(close);
        add(new MainPanel(), BorderLayout.CENTER);
    }
}

class MainPanel extends BackgroundPanel{
    public MainPanel() {
        framePanel = this;
        JLabel IDLabel = new JLabel("Enter your ID :");
        IDLabel.setFont(new Font("ARIAL", Font.BOLD, 25));
        IDLabel.setForeground(Color.WHITE);
        IDLabel.setBounds(100, 400, 400, 50);
        add(IDLabel);
        RoundTextField IDField = new RoundTextField(100, 450, 400, 40, 25, Color.WHITE, Color.WHITE, false);
        add(IDField);
        RoundButton loginButton = new RoundButton("Login", 250, 500, 100, 40, 25, Color.WHITE, blueColor, true);
        add(loginButton);
        RoundButton newUserButton = new RoundButton("New User ?", frameWidth - 320,450, 220, 50, 25, Color.white, Color.green, false);
        add(newUserButton);
        SwingUtilities.invokeLater(IDField::requestFocus);
        mainFrame.getRootPane().setDefaultButton(loginButton);

        try {
            BufferedImage image = ImageIO.read(new File("src/resources/Logo.png"));
            Image logo = image.getScaledInstance(frameWidth - 200, 300, Image.SCALE_SMOOTH);
            final JLabel imageLabel = new JLabel(new ImageIcon(logo));
            imageLabel.setBounds(100, 50, frameWidth - 200, 300);
            add(imageLabel);
        } catch (IOException e) {
            e.printStackTrace();
        }

        newUserButton.addActionListener(e -> {
            mainFrame.remove(framePanel);
            mainFrame.add(new Registration());
            mainFrame.repaint();
        });

        loginButton.addActionListener(e -> {
            String ID = IDField.getText();
            if (ID.equals("")) showErrorPopUp("Please Enter Valid ID !");
            else {
                // Write function to check validity of ID

                // If not valid
                // showErrorPopUp("Your msg to pop", IDField);

                // If valid
                mainFrame.remove(framePanel);
                mainFrame.add(new UserPanel());
                mainFrame.repaint();
            }
        });
    }
}
