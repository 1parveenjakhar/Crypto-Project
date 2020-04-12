package com;

import com.Transact.Transaction;
import com.utility.Util;
import com.wallet.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Block implements Serializable {
    // creates a block
    public String hash;
    public String previousBlockHash;
    public String merkleRoot;
    public ArrayList<Transaction> transactions = new ArrayList<>();
    public ArrayList<Transaction> failedTransactions = new ArrayList<>();
    private int nonce;
    public int blockNumber;
    private final long timestamp;

    public Block(String previousBlockHash, int blockNumber) {
        this.previousBlockHash = previousBlockHash;
        this.timestamp = new Date().getTime();
        this.blockNumber = blockNumber;
        this.hash = findHash();
    }

    public String findHash() {
        // calls the method that performs hashing
        return Util.hashAlgo(previousBlockHash + merkleRoot + timestamp + nonce);
    }

    public void mineBlock(int difficulty) {
        // mines the block by comparing the number of prefix zeroes in the hash of the block(with the help of a nonce)
        merkleRoot = Util.computeMerkleRoot(transactions);
        String required = new String(new char[difficulty]).replace('\0', '0');
        while(!hash.substring(0, difficulty).equals(required)) {
            nonce++;
            hash = findHash();
        }
        System.out.println("com.Block " + blockNumber +  " has been mined successfully!! " + hash);
    }

    public boolean addTransaction(Transaction t, User sender, User receiver) {
        // calls other methods that help in validating and then adding the transaction to a block
        if(t == null) {
            return false;
        }
        t.takeSignature(sender.getPrivateKey(), receiver.getPrivateKey());
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

    public void showUser(User u) {
        // shows all the failed as well as successful transaction of a given user
        ArrayList<Transaction> s = new ArrayList<>();
        for(Transaction t : transactions) {
            if(t.senderAddress.equals(u.getPublicKey()) || t.receiverAddress.equals(u.getPublicKey())) {
                s.add(t);
            }
        }
        ArrayList<Transaction> f = new ArrayList<>();
        for(Transaction t : failedTransactions) {
            if(t.senderAddress.equals(u.getPublicKey()) || t.receiverAddress.equals(u.getPublicKey())) {
                f.add(t);
            }
        }
        if(s.size() > 0) {
            System.out.println("Successful transactions in block: " + blockNumber);
            System.out.println(s);
        }
        if(f.size() > 0) {
            System.out.println("Failed transactions in block: " + blockNumber);
            System.out.println(f);
        }
    }
}
