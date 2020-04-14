package com.utility;

import com.MedicalChain;
import com.UI.MainFrame;

import javax.swing.*;
import java.awt.*;

public class CommonConstants {
    public static int frameWidth = 1200;
    public static int frameHeight = 600;
    public static Color blueColor = Color.decode("#013070");
    public static MainFrame mainFrame;
    public static JPanel framePanel;
    public static MedicalChain medicalChain;
    public static final int [] primes =  {18061, 18077, 18089, 18097, 18119, 18121, 18127, 18131, 18133, 18143, 18149, 18169, 18181, 18191, 18199, 18211, 18217, 18223, 18229, 18233};


    public static void showErrorPopUp(String msg) {
        JOptionPane.showMessageDialog(null, msg);
    }
}
