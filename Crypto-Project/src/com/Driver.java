package com;

import com.UI.MainFrame;

public class Driver {
    public static void main(String[] args) {
        // Create and display GUI from event dispatching thread (enhances thread safety)
        javax.swing.SwingUtilities.invokeLater(MainFrame::new);
    }
}
