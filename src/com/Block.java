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
    public int capacity;
    public int nonce;
    public int blockNumber;
    public long lastMinedTimeStamp;

    public Block(String previousBlockHash, int blockNumber) {
        this.previousBlockHash = previousBlockHash;
        this.blockNumber = blockNumber;
        this.hash = findHash();
        capacity = 2;
        lastMinedTimeStamp = -1;
    }

    public String findHash() {
        // calls the method that performs hashing
        return Util.hashAlgo(previousBlockHash + merkleRoot + lastMinedTimeStamp + nonce);
    }

    public void mineBlock(JLabel label) throws InterruptedException {
        String a = "- Transaction Verified Successfully !";
        String d = "- Transaction stored in block " + blockNumber;
        String b = "- Mining Block " + blockNumber + " ...";
        String c = "- Block " + blockNumber + " has been mined Successfully !";

        label.setForeground(Color.GREEN);
        label.setText("<html>" + a + "<br>" + d + "<br>" + b + "</html>");
        Thread.sleep(1000);

        // mines the block by comparing the number of prefix zeroes in the hash of the block(with the help of a nonce)
        merkleRoot = Util.computeMerkleRoot(transactions);
        String required = new String(new char[difficulty]).replace('\0', '0');
        while(!hash.substring(0, difficulty).equals(required)) {
            nonce++;
            hash = findHash();
        }
        lastMinedTimeStamp = new Date().getTime();

        label.setText("<html>" + a + "<br>" + d + "<br>" + b + "<br>" + c + "</html>");
        try {
            FileOutputStream fOut = new FileOutputStream(new File(chainPath));
            ObjectOutputStream objOut = new ObjectOutputStream(fOut);
            objOut.writeObject(medicalChain);
            objOut.close();
        } catch (Exception ex) {ex.printStackTrace();}
        capacity--;
    }

    public void addTransaction(Transaction t, JLabel label) throws InterruptedException {
        // calls other methods that help in validating and then adding the transaction to a block
        boolean failed = false;
        t.takeSignature(t.sender.getPrivateKey(), t.receiver.getPrivateKey());
        int count = 5;
        while(count != 0) {
            if (t.zeroKnowledgeProofFailure(t.sender) || t.zeroKnowledgeProofFailure(t.receiver)) {
                label.setText("<html>- Transaction is NOT Valid !<br>- Removed from Queue<br>- NOT stored in BlockChain</html>");
                label.setForeground(Color.RED);
                Thread.sleep(1000);
                failed = true;
                break;
            }
            count--;
        }
        if(!failed && !previousBlockHash.equals("0")) {
            if(!t.processTransaction()) {
                label.setText("<html>- Transaction is NOT Valid !<br>- Removed from Queue<br>- NOT stored in BlockChain</html>");
                label.setForeground(Color.RED);
                Thread.sleep(1000);
                failed = true;
            }
        }
        if (!failed) {transactions.add(t); mineBlock(label);}
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
