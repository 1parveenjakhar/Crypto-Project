package com.UI;

import com.Block;
import com.Transact.Transaction;
import com.utility.Util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.utility.CommonConstants.*;

public class BlockChain extends BackgroundPanel {
    private final BlockInfoPanel infoPanel;
    private final TransactionHistoryPanel historyPanel;
    private JPanel currentPanel;
    private final RoundButton changeButton;

    public BlockChain() {
        AtomicBoolean isHistoryView = new AtomicBoolean(true);
        framePanel = this;

        RoundButton backButton = new RoundButton("Back", 10, 10, 60, 25, 10, Color.cyan, blueColor, false);
        backButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        backButton.addActionListener(e -> {
            mainFrame.remove(framePanel);
            mainFrame.add(new MainPanel());
            mainFrame.repaint();
        });
        add(backButton);
        
        infoPanel = new BlockInfoPanel(true);
        historyPanel = new TransactionHistoryPanel(-1);
        currentPanel = historyPanel;
        add(infoPanel);
        add(historyPanel);
        infoPanel.setVisible(false);

        changeButton = new RoundButton("Deep Insight", frameWidth - 250, 50, 150, 40, 25, Color.CYAN, blueColor, false);
        changeButton.setFont(new Font(getName(), Font.BOLD, 15));
        add(changeButton);
        RoundButton[] actionButtons = new RoundButton[3];
        int width = (frameWidth - 40) / 3;
        for (int i = 0; i < 3; i++) {
            actionButtons[i] = new RoundButton("", 10 + (width + 10) * i, frameHeight - 70, width, 60, 25, Color.CYAN, blueColor, false);
            add(actionButtons[i]);
        }
        actionButtons[0].setText("<html>Block-wise View</html>");
        actionButtons[1].setText("<html>Next Block →</html>");
        actionButtons[2].setText("<html>← Previous Block</html>");
        actionButtons[1].setEnabled(false);
        actionButtons[2].setEnabled(false);

        actionButtons[0].addActionListener(e -> {
            currentPanel.setVisible(false);
            if (isHistoryView.get()) {
                actionButtons[0].setText("All Transactions");
                actionButtons[1].setEnabled(true);
                actionButtons[2].setEnabled(true);
                isHistoryView.set(false);
                changeButton.setText("Transactions");
                currentPanel = infoPanel;
            } else {
                historyPanel.reset(-1);
                actionButtons[0].setText("<html>Block-wise View</html>");
                actionButtons[1].setEnabled(false);
                actionButtons[2].setEnabled(false);
                isHistoryView.set(true);
                changeButton.setText("Deep Insight");
                historyPanel.reset(-1);
                currentPanel = historyPanel;
            }
            currentPanel.setVisible(true);
            mainFrame.repaint();
        });
        actionButtons[1].addActionListener(e -> infoPanel.showInfo(true));
        actionButtons[2].addActionListener(e -> infoPanel.showInfo(false));

        changeButton.addActionListener(e -> {
            if (isHistoryView.get()) {
                if (changeButton.getText().equals("Deep Insight")) {
                    changeButton.setText("Simple View");
                    historyPanel.deepInsight();
                } else {
                    changeButton.setText("Deep Insight");
                    historyPanel.reset(-1);
                }
            } else {
                if (changeButton.getText().equals("Block Info")) {
                    changeButton.setText("Transactions");
                    historyPanel.setVisible(false);
                    infoPanel.setVisible(true);
                    currentPanel = infoPanel;
                    isHistoryView.set(false);
                } else {
                    historyPanel.setVisible(true);
                    infoPanel.setVisible(false);
                    historyPanel.reset(infoPanel.last);
                    changeButton.setText("Block Info");
                }
            }
            mainFrame.repaint();
        });
    }
}


class BlockInfoPanel extends JPanel {
    public int last;
    private final JLabel blockLabel;
    public BlockInfoPanel(boolean next) {
        setLayout(null);
        setBounds(0, 0, frameWidth, frameHeight - 70);
        setOpaque(false);
        last = 0;
        blockLabel = new JLabel();
        blockLabel.setForeground(Color.decode("#42f58d"));
        blockLabel.setFont(new Font(getName(), Font.BOLD, 30));
        blockLabel.setBounds(100, 50, frameWidth - 220, 50);
        add(blockLabel);
        
        showInfo(next);
    }

    public void showInfo(boolean next) {
        if (next) last = (++last) % medicalChain.blockchain.size();
        else last = (--last) % medicalChain.blockchain.size();
        blockLabel.setText("Block " + last + " Info:");

        ArrayList<Block> blockchain = medicalChain.blockchain;
        JLabel hash = new JLabel("Block Hash - " + blockchain.get(last).hash);
        hash.setForeground(Color.WHITE);
        hash.setFont(new Font(getName(), Font.BOLD, 20));
        JLabel prevHash = new JLabel("Previous Hash - " + blockchain.get(last).previousBlockHash);
        prevHash.setForeground(Color.WHITE);
        prevHash.setFont(new Font(getName(), Font.BOLD, 20));
        JLabel merkleRoot = new JLabel("Merkle Root - " + blockchain.get(last).merkleRoot);
        merkleRoot.setForeground(Color.WHITE);
        merkleRoot.setFont(new Font(getName(), Font.BOLD, 20));
        JLabel nonce = new JLabel("nonce - " + blockchain.get(last).nonce);
        nonce.setForeground(Color.WHITE);
        nonce.setFont(new Font(getName(), Font.BOLD, 20));

        hash.setBounds(100, 120, frameWidth - 100, 50);
        prevHash.setBounds(100, 200, frameWidth - 100, 50);
        merkleRoot.setBounds(100, 280, frameWidth - 100, 50);
        nonce.setBounds(100, 360, frameWidth - 100, 50);

        add(hash);
        add(prevHash);
        add(merkleRoot);
        add(nonce);
    }
}

class TransactionHistoryPanel extends JPanel {
    private final JLabel historyLabel;
    public TransactionHistoryPanel(int i) {
        setLayout(null);
        setBounds(0, 0, frameWidth, frameHeight - 70);
        setOpaque(false);
        historyLabel = new JLabel("All transactions of BlockChain:");
        historyLabel.setForeground(Color.WHITE);
        historyLabel.setFont(new Font(getName(), Font.BOLD, 30));
        historyLabel.setBounds(100, 50, frameWidth - 220, 50);
        add(historyLabel);

        reset(i);
    }

    public void deepInsight() {
        for (Component c : this.getComponents())
            if (c != historyLabel)
                remove(c);
        historyLabel.setText("All transactions of BlockChain:");
        AllScrollHistory scrollHistoryPanel = new AllScrollHistory(-2);
        JScrollPane scrollPane = new JScrollPane(scrollHistoryPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBounds(100, 100, frameWidth - 200, frameHeight - 220);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        add(scrollPane);
    }

    public void reset(int i) {
        for (Component c : this.getComponents())
            if (c != historyLabel)
                remove(c);

        if (i == -1 || i ==-2) historyLabel.setText("All transactions of BlockChain:");
        else historyLabel.setText("Transactions of Block " + (i + 1) + ":");
        AllScrollHistory scrollHistoryPanel = new AllScrollHistory(i);
        JScrollPane scrollPane = new JScrollPane(scrollHistoryPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBounds(100, 100, frameWidth - 200, frameHeight - 220);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(12, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(12, 0));
        add(scrollPane);
    }
}

class AllScrollHistory extends JPanel {
    public AllScrollHistory(int i) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(10, 10, 0, 10));
        setOpaque(false);
        int count = 0;

        if (i == -2) {
            for (Transaction t : medicalChain.pendingToVerify) {
                String stringBuilder = "<html>" + "Prescription id: " + t.getPrescriptionID() + "<br>" +
                        "Time stamp :" + t.getTimeStamp() + "<br><br>" +
                        "Sender's address: " + Util.getKeyValue(t.sender.getPublicKey()) + "<br>" +
                        "Receiver's address: " + Util.getKeyValue(t.receiver.getPublicKey()) + "<br><br>" +
                        "Transaction Status - Pending</html>";
                RoundButton button = new RoundButton(stringBuilder, 40, Color.WHITE, Color.WHITE, 0.2f);
                button.setHorizontalAlignment(SwingConstants.LEFT);
                this.add(button);
                this.add(Box.createRigidArea(new Dimension(0, 10)));
                count++;
            }
            ArrayList<Transaction> list = new ArrayList<>();
            for (Block b : medicalChain.blockchain) list.addAll(b.transactions);
            for (Transaction t : list) {
                String stringBuilder = "<html>" + "Prescription id: " + t.getPrescriptionID() + "<br>" +
                        "Time stamp :" + t.getTimeStamp() + "<br><br>" +
                        "Sender's address: " + Util.getKeyValue(t.sender.getPublicKey()) + "<br>" +
                        "Receiver's address: " + Util.getKeyValue(t.receiver.getPublicKey()) + "<br>" + "</html>";
                RoundButton button = new RoundButton(stringBuilder, 40, Color.WHITE, Color.WHITE, 0.2f);
                button.setHorizontalAlignment(SwingConstants.LEFT);
                this.add(button);
                this.add(Box.createRigidArea(new Dimension(0, 10)));
                count++;
            }

        } else {
            if (i == -1) {
                for (Transaction t : medicalChain.pendingToVerify) {
                    StringBuilder stringBuilder = new StringBuilder("<html>");
                    stringBuilder.append("Doctor - ").append(t.sender.getUserDetails()[0]).append(" (ID : ").append(t.sender.getUserID()).append(")<br>");
                    stringBuilder.append("Patient - ").append(t.receiver.getUserDetails()[0]).append(" (ID : ").append(t.receiver.getUserID()).append(")<br><br>");
                    stringBuilder.append("Description :<br>");
                    stringBuilder.append(t.getDescription()).append("<br>");
                    if (t.getMedicines().size() > 0) {
                        stringBuilder.append("Medicines Referred :<br>");
                        int j = 1;
                        for (String s : t.getMedicines())
                            stringBuilder.append(j++).append(". ").append(s).append("<br>");
                    }
                    stringBuilder.append("<br>Transaction Status - Pending").append("<br></html>");

                    RoundButton button = new RoundButton(stringBuilder.toString(), 40, Color.WHITE, Color.WHITE, 0.2f);
                    button.setHorizontalAlignment(SwingConstants.LEFT);
                    this.add(button);
                    this.add(Box.createRigidArea(new Dimension(0, 10)));
                    count++;
                }
            }
            ArrayList<Transaction> list = new ArrayList<>();
            if (i == -1) for (Block b : medicalChain.blockchain) list.addAll(b.transactions);
            else list.addAll(medicalChain.blockchain.get(i).transactions);

            for (Transaction t : list) {
                StringBuilder stringBuilder = new StringBuilder("<html>");
                stringBuilder.append("Doctor - ").append(t.sender.getUserDetails()[0]).append(" (ID : ").append(t.sender.getUserID()).append(")<br>");
                stringBuilder.append("Patient - ").append(t.receiver.getUserDetails()[0]).append(" (ID : ").append(t.receiver.getUserID()).append(")<br><br>");
                stringBuilder.append("Description :<br>");
                stringBuilder.append(t.getDescription()).append("<br>");
                if (t.getMedicines().size() > 0) {
                    stringBuilder.append("Medicines Referred :<br>");
                    int j = 1;
                    for (String s : t.getMedicines())
                        stringBuilder.append(j++).append(". ").append(s).append("<br>");
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
            RoundButton button = new RoundButton("NO History for BlockChain !", 40, Color.WHITE, Color.WHITE, 0.2f);
            this.add(button);
            button.add(Box.createHorizontalGlue());
            button.setMargin(new Insets(20, 0, 20, 0));
            this.add(Box.createRigidArea(new Dimension(0, 10)));
        }
    }
}


