package com.UI;

import com.Block;
import com.Transact.Transaction;
import com.wallet.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

import static com.utility.CommonConstants.*;

public class UserPanel extends BackgroundPanel {
    private final JPanel infoPanel;
    private final HistoryPanel historyPanel;
    private JPanel transactionPanel = null;
    private JPanel currentPanel;
    private MinePanel minePanel;
    private final boolean isDoctor;

    public UserPanel(User user) {
        framePanel = this;
        isDoctor = user.getUserDetails()[3].equals("Doctor");
        infoPanel = new InfoPanel(user);
        historyPanel = new HistoryPanel(user);
        if (isDoctor) transactionPanel = new TransactionPanel(user);
        currentPanel = infoPanel;
        add(infoPanel);
        add(historyPanel);
        historyPanel.setVisible(false);
        if (isDoctor) {add(transactionPanel); transactionPanel.setVisible(false);}

        int count = 3;
        if (user.isMiner()) count++;
        if (isDoctor) count++;
        RoundButton[] actionButtons = new RoundButton[count];
        int height = (frameHeight - 5 * (count + 1)) / count;
        for (int i = 0; i < count; i++) {
            actionButtons[i] = new RoundButton("", 5, (height + 5) * i + 5, 200, height, 25, Color.CYAN, blueColor, false);
            add(actionButtons[i]);
        }
        if (user.isMiner()) {minePanel = new MinePanel(actionButtons); add(minePanel); minePanel.setVisible(false);}
        actionButtons[0].setText("User Info");
        actionButtons[1].setText("History");
        if (isDoctor) actionButtons[2].setText("Refer Medicines");
        if (user.isMiner()) actionButtons[count - 2].setText("<html>Mine A<br>Block</html>");
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
        else if (isDoctor && i == 2) {transactionPanel.setVisible(true); currentPanel = transactionPanel;}
        else {minePanel.reset(); minePanel.setVisible(true); currentPanel = minePanel;}
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
        scrollPane.setBounds(100, 125, frameWidth - 405, frameHeight - 175);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(12, 0));

        add(scrollPane);
    }
}

class ScrollHistory extends JPanel {
    public ScrollHistory(User user) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(10, 10, 0, 10));
        setOpaque(false);
        boolean isDoctor = user.getUserDetails()[3].equals("Doctor");
        int count = 0;

        for (Transaction t : medicalChain.pendingToVerify) {
            StringBuilder stringBuilder = new StringBuilder("<html>");
            User u = null;
            if (isDoctor && t.sender.getUserID().equals(user.getUserID())) u = t.receiver;
            else if (!isDoctor && t.receiver.getUserID().equals(user.getUserID())) u = t.sender;
            if (u == null)
                continue;
            stringBuilder.append((isDoctor) ? "Patient - " : "Doctor - ").append(u.getUserDetails()[0]).append(" (ID : ").append(u.getUserID()).append(")<br><br>");
            stringBuilder.append("Description :<br>");
            stringBuilder.append(t.getDescription()).append("<br>");
            if (t.getMedicines().size() > 0) {
                stringBuilder.append("Medicines Referred :<br>");
                int i = 1;
                for (String s : t.getMedicines())
                    stringBuilder.append(i++).append(". ").append(s).append("<br>");
            }
            stringBuilder.append("<br>Transaction Status - Pending").append("<br></html>");

            RoundButton button = new RoundButton(stringBuilder.toString(), 40, Color.WHITE, Color.WHITE, 0.2f);
            button.setHorizontalAlignment(SwingConstants.LEFT);
            this.add(button);
            this.add(Box.createRigidArea(new Dimension(0, 10)));
            count++;
        }

        for (Block block : medicalChain.blockchain) {
            ArrayList<Transaction> list = isDoctor ? block.getDoctorTransactions(user) : block.getPatientTransactions(user);
            for (Transaction t : list) {
                StringBuilder stringBuilder = new StringBuilder("<html>");
                User u = null;
                if (isDoctor && t.sender.getUserID().equals(user.getUserID())) u = t.receiver;
                else if (!isDoctor && t.receiver.getUserID().equals(user.getUserID())) u = t.sender;
                if (u == null)
                    continue;
                stringBuilder.append((isDoctor) ? "Patient - " : "Doctor - ").append(u.getUserDetails()[0]).append(" (ID : ").append(u.getUserID()).append(")<br><br>");
                stringBuilder.append("Description :<br>");
                stringBuilder.append(t.getDescription()).append("<br>");
                if (t.getMedicines().size() > 0) {
                    stringBuilder.append("Medicines Referred :<br>");
                    int i = 1;
                    for (String s : t.getMedicines())
                        stringBuilder.append(i++).append(". ").append(s).append("<br>");
                }
                stringBuilder.append("</html>");

                RoundButton button = new RoundButton(stringBuilder.toString(), 40, Color.WHITE, Color.WHITE, 0.2f);
                button.setHorizontalAlignment(SwingConstants.LEFT);
                this.add(button);
                this.add(Box.createRigidArea(new Dimension(0, 10)));
                count++;
            }
        }

        if (count == 0) {
            RoundButton button = new RoundButton("NO History for the User !", 40, Color.WHITE, Color.WHITE, 0.2f);
            this.add(button);
            button.add(Box.createHorizontalGlue());
            button.setMargin(new Insets(20, 0, 20, 0));
            this.add(Box.createRigidArea(new Dimension(0, 10)));
        }
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
                    ArrayList<String> list = new ArrayList<>();
                    for (String s : meds) if (!s.equals("")) list.add(s);
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String s : descArea.getText().split("\\n"))
                        stringBuilder.append(s).append("<br>");
                    Transaction newTransaction = new Transaction(user, patient, stringBuilder.toString(), list);
                    medicalChain.pendingToVerify.add(newTransaction);

                    FileOutputStream fileOut = new FileOutputStream(new File(chainPath));
                    ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
                    objectOut.writeObject(medicalChain);
                    objectOut.close();

                    showErrorPopUp("Transaction Successfully Added to Queue!");
                    medArea.setText("");
                    descArea.setText("");
                    userIDField.setText("");
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    showErrorPopUp("Transaction Failed!");
                    medArea.setText("");
                    descArea.setText("");
                    userIDField.setText("");
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

class MinePanel extends JPanel {
    private final RoundButton[] buttons;
    public MinePanel(RoundButton[] buttons) {
        setLayout(null);
        setBounds(205, 0, frameWidth - 205, frameHeight);
        setOpaque(false);
        this.buttons = buttons;
        reset();
    }

    public void  reset() {
        this.removeAll();
        int pending = medicalChain.pendingToVerify.size();
        JLabel mineLabel = new JLabel();
        if (pending == 0) mineLabel.setText("No transactions are pending now !");
        else if (pending  == 1) mineLabel.setText("1 Transaction is pending to Verify !");
        else mineLabel.setText((pending) + " Transactions are pending to Verify !");
        mineLabel.setForeground(Color.WHITE);
        mineLabel.setFont(new Font(getName(), Font.BOLD, 30));
        mineLabel.setBounds(100, 100, frameWidth - 405, 50);
        mineLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(mineLabel);
        int x = (frameWidth - 205 - 300) / 2;
        RoundButton verifyButton = new RoundButton("Verify Transactions", x, 200, 300, 50, 25, Color.CYAN, blueColor, false);
        add(verifyButton);

        JLabel statusLabel = new JLabel();
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font(getName(), Font.BOLD, 25));
        statusLabel.setBounds(100, 300, frameWidth - 405, 300);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setVerticalAlignment(SwingConstants.TOP);
        add(statusLabel);

        verifyButton.addActionListener(e -> {
            if (medicalChain.pendingToVerify.size() > 0) {
                statusLabel.setForeground(Color.WHITE);
                statusLabel.setText("Verifying, Please wait ...");
                mainFrame.disableButtons();
                Thread t = new Thread(() -> {
                    try {
                        for (RoundButton b : buttons)
                            b.setEnabled(false);
                        verifyButton.setEnabled(false);
                        medicalChain.verifyTransaction(statusLabel, mineLabel);
                        for (RoundButton b : buttons)
                            b.setEnabled(true);
                        verifyButton.setEnabled(true);
                        mainFrame.enableButtons();
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                        mainFrame.enableButtons();
                    }
                });
                t.start();

            } else {
                statusLabel.setText("");
            }
        });
    }
}

