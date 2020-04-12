package com.utility;

import com.UI.MainFrame;

import javax.swing.*;
import java.awt.*;

public class CommonConstants {
    public static int frameWidth = 1000;
    public static int frameHeight = 600;
    public static Color blueColor = Color.decode("#013070");
    public static MainFrame mainFrame;
    public static JPanel framePanel;

    public static void showErrorPopUp(String msg) {
        JOptionPane.showMessageDialog(null, msg);
    }
}
