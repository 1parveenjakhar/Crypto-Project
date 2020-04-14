package com.UI;

import com.wallet.User;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import static com.utility.CommonConstants.*;

public class Registration extends BackgroundPanel {
    private JTextField componentToFocus;
    
    public Registration() {
        framePanel = this;

        RoundButton backButton = new RoundButton("Back", 10, 10, 60, 25, 10, Color.white, blueColor, false);
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
            detailFields[i] = new RoundTextField(100, (space + 80) * i + 140 + space, 300, 40, 25, Color.CYAN);
            add(detailFields[i]);
        }
        SwingUtilities.invokeLater(() -> detailFields[0].requestFocus());


        JLabel minerLabel = new JLabel("Want to opt in as a Miner ?");
        minerLabel.setFont(new Font(getName(), Font.BOLD, 25));
        minerLabel.setForeground(Color.WHITE);
        minerLabel.setBounds(frameWidth - 420, frameHeight - 320, 350, 40);
        JCheckBox checkBox = new JCheckBox();
        checkBox.setBounds(frameWidth - 450, frameHeight - 320, 50, 40);
        checkBox.setOpaque(false);
        add(minerLabel);
        add(checkBox);

        

        RoundButton  registerButton = new RoundButton("Register", frameWidth - 250, frameHeight - 150, 120, 50, 25, Color.CYAN, blueColor, false);
        add(registerButton);
        mainFrame.getRootPane().setDefaultButton(registerButton);
        registerButton.addActionListener(e -> {
            try {
                // Checking validity of info entered for registration
                checkFieldValidity(detailFields);

                // If valid, then create user
                String ID = String.valueOf(1234 + medicalChain.users.size());
                User newUser = new User(detailFields[0].getText(), detailFields[1].getText(), detailFields[2].getText(),
                        detailFields[3].getText(), checkBox.isSelected(), ID);
                medicalChain.users.add(newUser);
                FileOutputStream fileOut = new FileOutputStream(new File(chainPath));
                ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
                objectOut.writeObject(medicalChain);
                objectOut.close();

                mainFrame.remove(framePanel);
                mainFrame.add(new UserPanel(newUser));
                mainFrame.repaint();
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
