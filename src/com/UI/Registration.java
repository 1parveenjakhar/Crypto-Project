package com.UI;

import javax.swing.*;
import java.awt.*;

import static com.utility.CommonConstants.*;

public class Registration extends BackgroundPanel {
    private JTextField componentToFocus;
    
    public Registration() {
        framePanel = this;

        RoundButton backButton = new RoundButton("←", 10, 10, 60, 25, 10, Color.white, blueColor, false);
        backButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        backButton.addActionListener(e -> {
            mainFrame.remove(framePanel);
            mainFrame.add(new MainPanel());
            mainFrame.repaint();
        });
        add(backButton);

        JLabel mainLabel = new JLabel("<html>Please fill up the details to Register:<html>");
        mainLabel.setBounds(100, 40, frameWidth - 200, 50);
        mainLabel.setFont(new Font("ARIAL", Font.BOLD, 40));
        mainLabel.setForeground(Color.CYAN);
        add(mainLabel);

        JLabel[] detailLabels = new JLabel[4];
        int space = (frameHeight - 100 - 4 * 80) / 5 - 5;
        for (int i = 0; i < 4; i++) {
            detailLabels[i] = new JLabel();
            detailLabels[i].setBounds(105, (space + 80) * i + 100 + space, 400, 40);
            detailLabels[i].setFont(new Font(getName(), Font.BOLD, 20));
            detailLabels[i].setForeground(Color.WHITE);
            add(detailLabels[i]);
        }
        detailLabels[0].setText("Name");
        detailLabels[1].setText("Age");
        detailLabels[2].setText("Gender (Male / Female / Other)");
        detailLabels[3].setText("Category (Doctor / Patient)");

        RoundTextField[] detailFields = new RoundTextField[4];
        for (int i = 0; i < 4; i++) {
            detailFields[i] = new RoundTextField(100, (space + 80) * i + 140 + space, 300, 40, 25, Color.CYAN, Color.WHITE, false);
            add(detailFields[i]);
        }
        SwingUtilities.invokeLater(() -> detailFields[0].requestFocus());


        /*JLabel userIDLabel = new JLabel("Your User ID :");
        userIDLabel.setFont(new Font(getName(), Font.BOLD, 25));
        userIDLabel.setForeground(Color.WHITE);
        userIDLabel.setBounds(frameWidth - 400, frameHeight - 320, 300, 40);

        JLabel userID = new JLabel();
        userID.setFont(new Font(getName(), Font.BOLD, 25));
        userID.setForeground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(userID, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBounds(frameWidth - 400, frameHeight - 370, 300, 40);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 8));*/

        RoundButton  registerButton = new RoundButton("Register", frameWidth - 250, frameHeight - 150, 120, 50, 25, Color.GREEN, blueColor, false);
        add(registerButton);
        mainFrame.getRootPane().setDefaultButton(registerButton);
        registerButton.addActionListener(e -> {
            try {
                // Checking validity of info entered for registration
                checkFieldValidity(detailFields);

                // if valid, then create user



            } catch (NumberFormatException nfe) {
                showErrorPopUp("Please enter valid age in range (0, 120) !");
                componentToFocus.requestFocus();
            } catch (Exception ex) {
                showErrorPopUp(ex.getMessage());
                componentToFocus.requestFocus();
            }
        });
    }
    
    private void checkFieldValidity(JTextField[] fields) throws Exception {
        if (fields[0].getText().equals("")) {
            componentToFocus = fields[0];
            throw new Exception("Please Enter a valid name !");
        } else if (fields[1].getText().equals("")) {
            componentToFocus = fields[1];
            throw new Exception("Please enter a valid age in range (0, 120) !");
        }
        int age = Integer.parseInt(fields[1].getText());
        String gender = fields[2].getText();
        String category = fields[3].getText();
        if (age <= 0 || age > 120) {
            componentToFocus = fields[1];
            throw new Exception("Please enter a valid age in range (0, 120) !");
        } else if(!(gender.equals("Male") || gender.equals("Female") || gender.equals("Other"))) {
            componentToFocus = fields[2];
            throw new Exception("Please enter a valid gender from - 'Male' or 'Female' or 'Other' !");
        } else if(!(category.equals("Doctor") || category.equals("Patient"))) {
            componentToFocus = fields[3];
            throw new Exception("Please enter a valid category from - 'Doctor' or 'Patient' !");
        }
    }
}
