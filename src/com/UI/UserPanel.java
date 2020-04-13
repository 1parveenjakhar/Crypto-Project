package com.UI;

import com.Block;
import com.Transact.Transaction;
import com.wallet.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static com.utility.CommonConstants.*;

public class UserPanel extends BackgroundPanel {
    private final JPanel infoPanel;
    private final HistoryPanel historyPanel;
    private JPanel transactionPanel = null;
    private JPanel currentPanel;

    public UserPanel(User user) {
        framePanel = this;
        boolean isDoctor = user.getUserDetails()[3].equals("Doctor");
        infoPanel = new InfoPanel(user);
        historyPanel = new HistoryPanel(user);
        if (isDoctor) transactionPanel = new TransactionPanel(user);
        currentPanel = infoPanel;
        add(infoPanel);
        add(historyPanel);
        historyPanel.setVisible(false);
        if (isDoctor) {add(transactionPanel); transactionPanel.setVisible(false);}

        int count = 3;
        if (isDoctor) count++;
        RoundButton[] actionButtons = new RoundButton[count];
        int height = (frameHeight - 5 * (count + 1)) / count;
        for (int i = 0; i < count; i++) {
            actionButtons[i] = new RoundButton("", 5, (height + 5) * i + 5, 200, height, 25, Color.CYAN, blueColor, false);
            add(actionButtons[i]);
        }
        actionButtons[0].setText("User Info");
        actionButtons[1].setText("History");
        if (isDoctor) actionButtons[2].setText("Refer Medicines");
        actionButtons[count - 1].setText("Logout");

        for (int i = 0; i < count - 1; i++) {
            int finalI = i;
            actionButtons[i].addActionListener(e -> changePanel(finalI, user));
        }
        actionButtons[count - 1].addActionListener(e -> {
            mainFrame.remove(framePanel);
            mainFrame.add(new MainPanel());
            mainFrame.repaint();
        });

    }

    private void changePanel(int i, User user) {
        currentPanel.setVisible(false);
        if (i == 0) {infoPanel.setVisible(true); currentPanel = infoPanel;}
        else if (i == 1) {historyPanel.reset(user); historyPanel.setVisible(true); currentPanel = historyPanel;}
        else {transactionPanel.setVisible(true); currentPanel = transactionPanel;}
        mainFrame.repaint();
    }
}


class InfoPanel extends JPanel {
    public InfoPanel(User user) {
        String[] details = user.getUserDetails();
        setLayout(null);
        setBounds(205, 0, frameWidth - 205, frameHeight);
        setOpaque(false);
        JLabel userID = new JLabel("User ID: " + user.getUserID());
        userID.setForeground(Color.WHITE);
        userID.setFont(new Font(getName(), Font.BOLD, 30));
        userID.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
        JLabel name = new JLabel("Name - " + details[0]);
        name.setForeground(Color.WHITE);
        name.setFont(new Font(getName(), Font.BOLD, 20));
        JLabel age = new JLabel("Age - " + details[1]);
        age.setForeground(Color.WHITE);
        age.setFont(new Font(getName(), Font.BOLD, 20));
        JLabel gender = new JLabel("Gender - " + details[2]);
        gender.setForeground(Color.WHITE);
        gender.setFont(new Font(getName(), Font.BOLD, 20));
        JLabel category = new JLabel("Category - " + details[3]);
        category.setForeground(Color.WHITE);
        category.setFont(new Font(getName(), Font.BOLD, 20));

        userID.setBounds(100, 150, frameWidth - 405, 80);
        userID.setHorizontalAlignment(SwingConstants.CENTER);
        name.setBounds(100, 350, 400, 50);
        age.setBounds(500, 350, 200, 50);
        gender.setBounds(100, 450, 250, 50);
        category.setBounds(500, 450, 250, 50);

        add(userID);
        add(name);
        add(age);
        add(gender);
        add(category);
    }
}

class HistoryPanel extends JPanel {
    private final JLabel historyLabel;
    public HistoryPanel(User user) {
        setLayout(null);
        setBounds(205, 0, frameWidth - 205, frameHeight);
        setOpaque(false);

        historyLabel = new JLabel("History of Medical Check-Up:");
        historyLabel.setForeground(Color.WHITE);
        historyLabel.setFont(new Font(getName(), Font.BOLD, 30));
        historyLabel.setBounds(100, 75, frameWidth - 405, 50);
        add(historyLabel);

        reset(user);
    }

    public void reset(User user) {
        for (Component c : this.getComponents())
            if (c != historyLabel)
                remove(c);
        ScrollHistory scrollHistoryPanel = new ScrollHistory(user);
        JScrollPane scrollPane = new JScrollPane(scrollHistoryPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBounds(95, 125, frameWidth - 395, frameHeight - 175);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));

        add(scrollPane);
    }
}

class ScrollHistory extends JPanel {
    public ScrollHistory(User user) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(10, 10, 0, 10));
        setOpaque(false);
        boolean isDoctor = user.getUserDetails()[3].equals("Doctor");

        for (Block block : medicalChain.blockchain) {
            Map<Transaction, Boolean> map = isDoctor ? block.getDoctorTransactions(user) : block.getPatientTransactions(user);
            for (Map.Entry<Transaction, Boolean> entry : map.entrySet()) {
                StringBuilder stringBuilder = new StringBuilder("<html>");
                User u = getUser(entry.getKey(), isDoctor);
                if (u == null)
                    continue;
                stringBuilder.append((isDoctor) ? "Patient - " : "Doctor - ").append(u.getUserDetails()[0]).append(" (ID : ").append(u.getUserID()).append(")<br><br>");
                stringBuilder.append("Description :<br>");
                stringBuilder.append(entry.getKey().getDescription()).append("<br>");
                stringBuilder.append("Medicines Referred :<br>");
                int i = 1;
                for (String s : entry.getKey().getMedicines())
                    stringBuilder.append(i++).append(". ").append(s).append("<br>");
                stringBuilder.append("<br>Transaction Status - ").append((entry.getValue() ? "Success" : "NOT Successful !")).append("<br></html>");

                RoundButton button = new RoundButton(stringBuilder.toString(), 40, Color.WHITE, Color.BLACK);
                button.setHorizontalAlignment(SwingConstants.LEFT);
                this.add(button);
                this.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }
    }

    private User getUser(Transaction t, boolean isDoctor) {
        for (User user : medicalChain.users) {
            if (isDoctor && user.getPublicKey().equals(t.getReceiverAddress()))
                return user;
            if (!isDoctor && user.getPublicKey().equals(t.getSenderAddress()))
                return user;
        }
        return null;
    }
}

class TransactionPanel extends JPanel {
    public TransactionPanel(User user) {
        setLayout(null);
        setBounds(205, 0, frameWidth - 205, frameHeight);
        setOpaque(false);

        JLabel toUserLabel = new JLabel("=> User ID of Patient:");
        toUserLabel.setForeground(Color.WHITE);
        toUserLabel.setVerticalTextPosition(SwingConstants.CENTER);
        toUserLabel.setVerticalAlignment(SwingConstants.CENTER);
        toUserLabel.setFont(new Font(getName(), Font.BOLD, 25));
        toUserLabel.setBounds(100, 50, 300, 40);
        add(toUserLabel);

        RoundTextField userIDField = new RoundTextField(370, 50, 150, 35, 25, Color.WHITE);
        userIDField.setAlignmentY(CENTER_ALIGNMENT);
        userIDField.setCaretColor(Color.WHITE);
        userIDField.setHorizontalAlignment(SwingConstants.CENTER);
        add(userIDField);

        JLabel description = new JLabel("Description of disease:");
        description.setForeground(Color.WHITE);
        description.setFont(new Font(getName(), Font.BOLD, 25));
        description.setBounds(100, 120, frameWidth - 405, 40);
        add(description);
        JTextArea descArea = new JTextArea();
        descArea.setEditable(true);
        descArea.setFont(new Font(getName(), Font.BOLD, 20));
        descArea.setForeground(Color.CYAN);
        descArea.setOpaque(false);
        descArea.setBackground(new Color(0, 0, 0, 0));
        descArea.setCaretColor(Color.WHITE);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane descriptionPane = new JScrollPane(descArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        descriptionPane.getViewport().setOpaque(false);
        descriptionPane.setOpaque(false);
        descriptionPane.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
        descriptionPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        descriptionPane.setBounds(100, 160, frameWidth - 405, 140);
        add(descriptionPane);

        JLabel medicines = new JLabel("Refer medicines, one per line:");
        medicines.setForeground(Color.WHITE);
        medicines.setFont(new Font(getName(), Font.BOLD, 25));
        medicines.setBounds(100, 325, frameWidth - 405, 40);
        add(medicines);
        JTextArea medArea = new JTextArea();
        medArea.setEditable(true);
        medArea.setFont(new Font(getName(), Font.BOLD, 20));
        medArea.setForeground(Color.CYAN);
        medArea.setOpaque(false);
        medArea.setBackground(new Color(0, 0, 0, 0));
        medArea.setCaretColor(Color.WHITE);
        medArea.setLineWrap(true);
        medArea.setWrapStyleWord(true);
        medArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane medicinePane = new JScrollPane(medArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        medicinePane.getViewport().setOpaque(false);
        medicinePane.setOpaque(false);
        medicinePane.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
        medicinePane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        medicinePane.setBounds(100, 370, frameWidth - 405, 140);
        add(medicinePane);

        RoundButton transactionButton = new RoundButton("Refer Medicines", frameWidth - 500, frameHeight - 60, 200, 40, 25, Color.CYAN, Color.BLACK, false);
        add(transactionButton);
        mainFrame.getRootPane().setDefaultButton(transactionButton);

        transactionButton.addActionListener(e -> {
            try {
                // Function to check validity of details filled by doctor
                User patient = checkDoctorPrescriptionValidity(userIDField, descArea);


                // Code to add new transaction here
                try {
                    String[] meds = medArea.getText().split("\\n");
                    ArrayList<String> list = new ArrayList<>(Arrays.asList(meds));
                    System.out.println(list);
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String s : descArea.getText().split("\\n"))
                        stringBuilder.append(s).append("<br>");
                    Transaction newTransaction = new Transaction(user.getPublicKey(), patient.getPublicKey(), stringBuilder.toString(), list);
                    int blockNumber = medicalChain.blockchain.size();
                    String prevHash = (blockNumber == 0) ? "0" : medicalChain.blockchain.get(blockNumber - 1).hash;
                    Block newBlock = new Block(prevHash, blockNumber + 1);
                    if (!newBlock.addTransaction(newTransaction, user, patient)) throw new Exception();
                    medicalChain.addBlock(newBlock);
                    FileOutputStream fileOut = new FileOutputStream(new File("./src/Resources/BlockChain"));
                    ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
                    objectOut.writeObject(medicalChain);
                    objectOut.close();

                    showErrorPopUp("Transaction Successfully Added!");
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    showErrorPopUp("Transaction Failed!");
                }
            } catch (Exception ex) {
                showErrorPopUp(ex.getMessage());
            }
        });
    }

    private User checkDoctorPrescriptionValidity(JTextField ID, JTextArea desc) throws Exception {
        User user = null;
        // Checking patient id existence
        if (ID.getText().equals(""))
            throw new Exception("Please enter valid Patient ID !");

        boolean patientExists = false;
        for (User u : medicalChain.users)
            if (u.getUserID().equals(ID.getText())) {
                patientExists = true;
                user = u;
                break;
            }
        if (!patientExists)
            throw new Exception("Patient ID not exists!");

        if (desc.getText().equals(""))
            throw new Exception("Please Enter some description of disease !");

        return user;
    }
}

