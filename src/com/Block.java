package com;

import com.Transact.Transaction;
import com.utility.Util;
import com.wallet.User;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

import static com.utility.CommonConstants.*;

public class Block implements Serializable {
    // creates a block
    public String hash;
    public String previousBlockHash;
    public String merkleRoot;
    public ArrayList<Transaction> transactions = new ArrayList<>();
    public int nonce;
    public int blockNumber;
    public long lastMinedTimeStamp;

    public Block(String previousBlockHash, int blockNumber) {
        this.previousBlockHash = previousBlockHash;
        this.blockNumber = blockNumber;
        this.hash = findHash();
        lastMinedTimeStamp = -1;
    }

    public String findHash() {
        // calls the method that performs hashing
        return Util.hashAlgo(previousBlockHash + merkleRoot + lastMinedTimeStamp + nonce);
    }

    public void mineBlock(JLabel label) throws InterruptedException {
        String d = "- All successful Transactions are stored in Block " + blockNumber;
        String b = "- Mining Block " + blockNumber + " ...";
        String c = "- Block " + blockNumber + " has been mined Successfully !";
        String a = null;

        if (label != null) {
            a = label.getText();
            label.setForeground(Color.GREEN);
            label.setText(a + "<html><br>" + d + "<br><br>" + b + "</html>");
            Thread.sleep(1000);
        }

        // mines the block by comparing the number of prefix zeroes in the hash of the block(with the help of a nonce)
        if (transactions.size() == 0) merkleRoot = "NULL";
        else merkleRoot = Util.computeMerkleRoot(transactions);
        String required = new String(new char[difficulty]).replace('\0', '0');
        while(!hash.substring(0, difficulty).equals(required)) {
            nonce++;
            hash = findHash();
        }
        lastMinedTimeStamp = new Date().getTime();
        if (label != null) label.setText(a + "<html><br>" + d + "<br><br>" + b + "<br>" + c + "</html>");
        try {
            FileOutputStream fOut = new FileOutputStream(new File(chainPath));
            ObjectOutputStream objOut = new ObjectOutputStream(fOut);
            objOut.writeObject(medicalChain);
            objOut.close();
        } catch (Exception ex) {ex.printStackTrace();}
    }

    public void addTransaction(JLabel label, JLabel mainLabel) throws InterruptedException {
        // calls other methods that help in validating and then adding the transaction to a block
        int x = 0;
        int pending = medicalChain.pendingToVerify.size();
        for (int i = 1; i <= pending; i++) {
            Transaction t = medicalChain.pendingToVerify.remove();
            boolean failed = false;
            t.takeSignature(t.sender.getPrivateKey(), t.receiver.getPrivateKey());
            int count = 5;
            while (count != 0) {
                if (t.zeroKnowledgeProofFailure(t.sender) || t.zeroKnowledgeProofFailure(t.receiver)) {
                    failed = true;
                    break;
                }
                count--;
            }
            if (!failed && !previousBlockHash.equals("0")) {
                if (!t.processTransaction()) {
                    failed = true;
                }
            }
            if (!failed) {
                transactions.add(t); x++;
            }
            if (pending - i == 0) mainLabel.setText("NO Transactions are pending !");
            else if (pending - i == 1) mainLabel.setText("1 Transaction is pending to verify !");
            else mainLabel.setText((pending - i) + " Transactions are pending to verify !");
            Thread.sleep(1000);
        }

        if (x > 0) {
            label.setForeground(Color.green);
            label.setText("<html>Total Valid Transactions = " + x + "<br>Total Invalid Transactions = " + (pending - x) + "<br><html>");
            mineBlock(label);
        }
        else {
            label.setForeground(Color.RED);
            label.setText("All transactions are invalid !");
        }
    }

    public ArrayList<Transaction> getPatientTransactions(User u) {
        // shows only the successful transaction of a given patient
        ArrayList<Transaction> list = new ArrayList<>();
        for(Transaction t : transactions) {
            if(t.receiver.getPublicKey().equals(u.getPublicKey())) {
                list.add(t);
            }
        }
        return list;
    }

    public ArrayList<Transaction> getDoctorTransactions(User u) {
        // shows only the successful transactions made by a given doctor
        ArrayList<Transaction> list = new ArrayList<>();
        for(Transaction t : transactions) {
            if(t.sender.getPublicKey().equals(u.getPublicKey())) {
                list.add(t);
            }
        }
        return list;
    }
}
