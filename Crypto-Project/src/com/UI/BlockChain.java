package com.UI;

import com.Block;
import com.Transact.Transaction;
import com.utility.Util;
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

public class BlockChain extends BackgroundPanel {
    private JPanel infoPanel;
    private final TransactionHistoryPanel historyPanel;
    private JPanel transactionPanel = null;
    private JPanel currentPanel;
    private ArrayList<Block> blockchain;
    private int count = 4;
    private RoundButton[] actionButtons = new RoundButton[count];
    private int index;

    public BlockChain(ArrayList<Block> chain) {
        blockchain = chain;
        index = blockchain.size()-1;
        framePanel = this;
        infoPanel = new BlockInfoPanel(blockchain, index);
        historyPanel = new TransactionHistoryPanel(index);
        currentPanel = infoPanel;
        add(infoPanel);
        add(historyPanel);
        historyPanel.setVisible(false);

        int height = (frameHeight - 5 * (count + 1)) / count;
        for (int i = 0; i < count; i++) {
            actionButtons[i] = new RoundButton("", 5, (height + 5) * i + 5, 200, height, 25, Color.CYAN, blueColor, false);
            add(actionButtons[i]);
        }
        actionButtons[0].setText("Transaction");
        actionButtons[1].setText("Next Block");
        actionButtons[2].setText("Previous Block");
        actionButtons[count - 1].setText("Back");

        for (int i = 0; i < count - 1; i++) {
            int finalI = i;
            actionButtons[i].addActionListener(e -> changePanel(finalI));
        }
        actionButtons[count - 1].addActionListener(e -> {
            mainFrame.remove(framePanel);
            mainFrame.add(new MainPanel());
            mainFrame.repaint();
        });

    }

    private void changePanel(int i) {
        currentPanel.setVisible(false);
        if (i == 0) {
            if(actionButtons[0].getText().equals("Transaction")) {
                historyPanel.reset(index);
                historyPanel.setVisible(true); currentPanel = historyPanel;
                actionButtons[0].setText("View Block");
            }
            else {
                infoPanel.setVisible(true); currentPanel = infoPanel;
                actionButtons[0].setText("Transaction");
            }
        }
        else if (i == 1) {
            actionButtons[0].setText("Transaction");
            int total = blockchain.size();
            index = (index + 1) % total;
            infoPanel = new BlockInfoPanel(blockchain, index);
            currentPanel = infoPanel;
            add(infoPanel);
            infoPanel.setVisible(true);
        }
        else {
            actionButtons[0].setText("Transaction");
            int total = blockchain.size();
            index = (((index - 1) % total) + total) % total;
            infoPanel = new BlockInfoPanel(blockchain, index);
            add(infoPanel);
            infoPanel.setVisible(true);
            currentPanel = infoPanel;
        }
        mainFrame.repaint();
    }
}


class BlockInfoPanel extends JPanel {
    public ArrayList<Block> blockchain;
    private int index;
    public BlockInfoPanel(ArrayList<Block> chain, int i) {
        blockchain = chain;
        index = i;
        setLayout(null);
        setBounds(205, 0, frameWidth - 205, frameHeight);
        setOpaque(false);
        JLabel blockID = new JLabel("Block Number: " + blockchain.get(i).blockNumber);
        blockID.setForeground(Color.WHITE);
        blockID.setFont(new Font(getName(), Font.BOLD, 30));
        blockID.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
        JLabel hash = new JLabel("Block Hash - " + blockchain.get(i).hash);
        hash.setForeground(Color.WHITE);
        hash.setFont(new Font(getName(), Font.BOLD, 20));
        JLabel prevHash = new JLabel("Previous Hash - " + blockchain.get(i).previousBlockHash);
        prevHash.setForeground(Color.WHITE);
        prevHash.setFont(new Font(getName(), Font.BOLD, 20));
        JLabel merkleRoot = new JLabel("Merkle Root - " + blockchain.get(i).merkleRoot);
        merkleRoot.setForeground(Color.WHITE);
        merkleRoot.setFont(new Font(getName(), Font.BOLD, 20));
        JLabel nonce = new JLabel("nonce - " + blockchain.get(i).nonce);
        nonce.setForeground(Color.WHITE);
        nonce.setFont(new Font(getName(), Font.BOLD, 20));
        blockID.setBounds(100, 150, frameWidth - 405, 80);
        blockID.setHorizontalAlignment(SwingConstants.CENTER);
        hash.setBounds(25, 250, 1000, 50);
        prevHash.setBounds(25, 325, 1000, 50);
        merkleRoot.setBounds(25, 400, 1000, 50);
        nonce.setBounds(25, 475, 550, 50);

        add(blockID);
        add(hash);
        add(prevHash);
        add(merkleRoot);
        add(nonce);
    }
}

class TransactionHistoryPanel extends JPanel {
    private final JLabel historyLabel;
    private int index;
    public TransactionHistoryPanel(int i) {
        index = i;
        setLayout(null);
        setBounds(205, 0, frameWidth - 205, frameHeight);
        setOpaque(false);

        historyLabel = new JLabel("History of Medical Check-Up:");
        historyLabel.setForeground(Color.WHITE);
        historyLabel.setFont(new Font(getName(), Font.BOLD, 30));
        historyLabel.setBounds(100, 75, frameWidth - 405, 50);
        add(historyLabel);

        reset(index);
    }

    public void reset(int i) {
        for (Component c : this.getComponents())
            if (c != historyLabel)
                remove(c);
        AllScrollHistory scrollHistoryPanel = new AllScrollHistory(i);
        JScrollPane scrollPane = new JScrollPane(scrollHistoryPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBounds(95, 125, frameWidth - 395, frameHeight - 175);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));

        add(scrollPane);
    }
}

class AllScrollHistory extends JPanel {
    public AllScrollHistory(int i) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(10, 10, 0, 10));
        setOpaque(false);
        Block block = medicalChain.blockchain.get(i);
        {
            RoundButton button = new RoundButton("SUCCESSFUL TRANSACTIONS", 40, Color.WHITE, blueColor);
            button.setHorizontalAlignment(SwingConstants.LEFT);
            this.add(button);
            this.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        for (Transaction t : block.transactions) {
            StringBuilder stringBuilder = new StringBuilder("<html>");
            stringBuilder.append("Prescription id: ").append(t.getPrescriptionID()).append("<br><br>");
            stringBuilder.append("Time stamp :").append(t.getTimeStamp()).append("<br>");
            stringBuilder.append("Reciever's address: ").append(Util.getKeyValue(t.getReceiverAddress())).append("<br>");
            stringBuilder.append("Sender's address: ").append(Util.getKeyValue(t.getSenderAddress())).append("<br>").append("<br></html>");

            RoundButton button = new RoundButton(stringBuilder.toString(), 40, Color.WHITE, Color.BLACK);
            button.setHorizontalAlignment(SwingConstants.LEFT);
            this.add(button);
            this.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        if(block.transactions.size() == 0) {
            RoundButton button = new RoundButton("NONE", 40, Color.WHITE, Color.BLACK);
            button.setHorizontalAlignment(SwingConstants.LEFT);
            this.add(button);
            this.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        {
            RoundButton button = new RoundButton("UNSUCCESSFUL TRANSACTIONS", 40, Color.WHITE, blueColor);
            button.setHorizontalAlignment(SwingConstants.LEFT);
            this.add(button);
            this.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        for(Transaction t : block.failedTransactions) {
            StringBuilder stringBuilder = new StringBuilder("<html>");
            stringBuilder.append("Prescription id: ").append(t.getPrescriptionID()).append("<br><br>");
            stringBuilder.append("Time stamp :").append(t.getTimeStamp()).append("<br>");
            stringBuilder.append("Reciever's address: ").append(t.getReceiverAddress()).append("<br>");
            stringBuilder.append("Sender's address: ").append(t.getSenderAddress()).append("<br>").append("<br></html>");

            RoundButton button = new RoundButton(stringBuilder.toString(), 40, Color.WHITE, Color.BLACK);
            button.setHorizontalAlignment(SwingConstants.LEFT);
            this.add(button);
            this.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        if(block.failedTransactions.size() == 0) {
            RoundButton button = new RoundButton("NONE", 40, Color.WHITE, Color.BLACK);
            button.setHorizontalAlignment(SwingConstants.LEFT);
            this.add(button);
            this.add(Box.createRigidArea(new Dimension(0, 10)));
        }
    }
}


