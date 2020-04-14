package com;

import com.Transact.Transaction;
import com.utility.Util;
import com.wallet.User;

import javax.swing.*;
import java.io.Serializable;
import java.util.*;

public class Block implements Serializable {
    // creates a block
    public String hash;
    public String previousBlockHash;
    public String merkleRoot;
    public ArrayList<Transaction> transactions = new ArrayList<>();
    public ArrayList<Transaction> failedTransactions = new ArrayList<>();
    public int nonce;
    public int blockNumber;

    public Block(String previousBlockHash, int blockNumber) {
        this.previousBlockHash = previousBlockHash;
        this.blockNumber = blockNumber;
        this.hash = findHash();
    }

    public String findHash() {
        // calls the method that performs hashing
        return Util.hashAlgo(previousBlockHash + merkleRoot + nonce);
    }

    public void mineBlock(int difficulty) {
        // mines the block by comparing the number of prefix zeroes in the hash of the block(with the help of a nonce)
        merkleRoot = Util.computeMerkleRoot(transactions);
        String required = new String(new char[difficulty]).replace('\0', '0');
        while(!hash.substring(0, difficulty).equals(required)) {
            nonce++;
            hash = findHash();
        }
        System.out.println("com.com.Block " + blockNumber +  " has been mined successfully!! " + hash);
    }

    public boolean addTransaction(Transaction t, User sender, User receiver) {
        // calls other methods that help in validating and then adding the transaction to a block
        if(t == null) {
            return false;
        }
        t.takeSignature(sender.getPrivateKey(), receiver.getPrivateKey());
        System.out.println("Validating Transaction...\nplease wait...");
        int count = 5;
        while(count != 0) {
            if (t.zeroKnowledgeProofFailure(sender) || t.zeroKnowledgeProofFailure(receiver)) {
                System.out.println("Transaction failed!");
                failedTransactions.add(t);
                return false;
            }
            count--;
        }
        if(!previousBlockHash.equals("0")) {
            if(!t.processTransaction()) {
                System.out.println("Transaction failed!");
                failedTransactions.add(t);
                return false;
            }
        }
        transactions.add(t);
        System.out.println("Transaction Successfully added to the block");
        return true;
    }

    public HashMap<Transaction, Boolean> getPatientTransactions(User u) {
        // shows all the failed as well as successful transaction of a given user
        HashMap<Transaction, Boolean> map = new HashMap<>();
        for(Transaction t : transactions) {
            if(t.getReceiverAddress().equals(u.getPublicKey())) {
                map.put(t, true);
            }
        }
        for(Transaction t : failedTransactions) {
            if(t.getReceiverAddress().equals(u.getPublicKey())) {
                map.put(t, false);
            }
        }
        return map;
    }

    public HashMap<Transaction, Boolean> getDoctorTransactions(User u) {
        // shows all the failed as well as successful transaction of a given user
        HashMap<Transaction, Boolean> map = new HashMap<>();
        for(Transaction t : transactions) {
            if(t.getSenderAddress().equals(u.getPublicKey())) {
                map.put(t, true);
            }
        }
        for(Transaction t : failedTransactions) {
            if(t.getSenderAddress().equals(u.getPublicKey())) {
                map.put(t, false);
            }
        }
        return map;
    }
}
