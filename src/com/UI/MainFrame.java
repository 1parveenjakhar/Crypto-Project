package com.UI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

import static com.utility.CommonConstants.*;
import com.MedicalChain;
import com.wallet.User;


public class MainFrame extends JFrame {
    private final RoundButton minimize;
    private final RoundButton close;
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

        minimize = new RoundButton("_", frameWidth - 115, 5, 50, 20, 10, Color.cyan, blueColor, false);
        close = new RoundButton("X", frameWidth - 55, 5, 50, 20, 10, Color.cyan, Color.red, false);
        close.setFont(new Font("ARIAL", Font.BOLD, 13));
        minimize.setFont(new Font("ARIAL", Font.BOLD, 15));
        // Fix cross platform rendering issues. Displays on Windows only.
        minimize.setMargin(new Insets(0, 0, 7, 0));
        close.addActionListener(e -> System.exit(0));
        minimize.addActionListener(e -> setState(JFrame.ICONIFIED));

        add(minimize);
        add(close);
        add(new MainPanel(), BorderLayout.CENTER);

        // Add a new BlockChain, if not present
        try {
            File f = new File(chainPath);
            if (!f.exists()) {
                medicalChain = MedicalChain.getInstance();
                ObjectOutputStream objOut = new ObjectOutputStream(new FileOutputStream(f));
                objOut.writeObject(medicalChain);
                objOut.close();
            } else {
                FileInputStream fIn = new FileInputStream(f);
                ObjectInputStream objIn = new ObjectInputStream(fIn);
                medicalChain = (MedicalChain) objIn.readObject();
                objIn.close();
            }
        } catch (Exception e) {
            System.out.println("Exception occur on your System: " + e.getMessage());
        }
    }

    public void disableButtons() {
        minimize.setEnabled(false);
        close.setEnabled(false);
    }

    public void enableButtons() {
        minimize.setEnabled(true);
        close.setEnabled(true);
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
        RoundTextField IDField = new RoundTextField(100, 450, 400, 40, 25, Color.WHITE);
        add(IDField);
        RoundButton loginButton = new RoundButton("Login", 250, 500, 100, 40, 25, Color.CYAN, blueColor, false);
        add(loginButton);
        RoundButton newUserButton = new RoundButton("New User ?", frameWidth - 320,450, 220, 50, 25, Color.BLACK, Color.green, false);
        add(newUserButton);
        RoundButton viewBlockchainButton = new RoundButton("View BlockChain", frameWidth - 320,520, 220, 50, 25, Color.BLACK, Color.green, false);
        add(viewBlockchainButton);
        SwingUtilities.invokeLater(IDField::requestFocus);
        mainFrame.getRootPane().setDefaultButton(loginButton);

        try {
            BufferedImage image = ImageIO.read(this.getClass().getResource(logoPath));
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
                try {
                    // Function to check validity of ID
                    User user = checkExistenceOfUser(ID);

                    // If valid
                    mainFrame.remove(framePanel);
                    mainFrame.add(new UserPanel(user));
                    mainFrame.repaint();
                } catch (Exception ex) {
                    showErrorPopUp(ex.getMessage());
                }
            }
        });

        viewBlockchainButton.addActionListener(e -> {
            if (medicalChain.blockchain.get(0).transactions.size() == 0 && medicalChain.pendingToVerify.size() == 0)
                showErrorPopUp("NO Transactions has been made yet !");
            else {
                mainFrame.remove(framePanel);
                mainFrame.add(new BlockChain());
                mainFrame.repaint();
            }
        });
    }

    private User checkExistenceOfUser(String ID) throws Exception {
        try {
            ArrayList<User> list = medicalChain.users;

            // If user present then no exception
            if (list != null)
                for (User user : list)
                    if (user.getUserID().equals(ID))
                        return user;

            // else throw exception
            throw new Exception("User does not exist!");
        } catch (IOException | NullPointerException e ) {
            throw new Exception("Unable to retrieve user info !");
        }
    }
}
