package com;

import com.Transact.Transaction;
import com.wallet.User;

import javax.swing.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import static com.utility.CommonConstants.difficulty;

public class MedicalChain implements Serializable {
    // it contains the main blockchain
    // it is a singleton class
    public ArrayList<Block> blockchain;
    public ArrayList<User> users;
    public Queue<Transaction> pendingToVerify;
    private static boolean valid = true;
    private static MedicalChain instance;

    private MedicalChain() {
        blockchain = new ArrayList<>();
        users = new ArrayList<>();
        instance = this;
        pendingToVerify = new LinkedList<>();
        blockchain.add(new Block("0", 1));
    }

    public static MedicalChain getInstance() throws Exception {
        if (instance == null)
            return new MedicalChain();
        if (!valid)
            throw new Exception("<html>Your BlockChain is not valid!<br/>Probably, it has been tampered!</html>");
        return instance;
    }

    public boolean isChainValid() {
        // checks if the blockchain is tampered or not
        Block previousBlock, currentBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        for(int i = 1; i < blockchain.size(); i++) {
            previousBlock = blockchain.get(i-1);
            currentBlock = blockchain.get(i);
            if(!currentBlock.previousBlockHash.equals(previousBlock.hash)) {
                System.out.println("Previous hashes not equal");
                valid = false;
                return false;
            }
            if(!currentBlock.hash.equals(currentBlock.findHash())) {
                System.out.println("Current hashes not equal");
                valid = false;
                return false;
            }
            if(!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
                System.out.println("com.Block number: " + blockchain.get(i).blockNumber + " is not mined");
                valid = false;
                return false;
            }
            for(int j = 0; j < currentBlock.transactions.size(); j++) {
                Transaction current = currentBlock.transactions.get(j);
                if(current.verifySignature(current.sender.getPublicKey(), current.receiver.getPublicKey())) {
                    System.out.println("Signature on Transaction: " + j + " is invalid");
                    valid = false;
                    return false;
                }
            }
        }
        System.out.println("Blockchain is valid");
        return true;
    }

    public void verifyTransaction(JLabel label) throws InterruptedException {
        // verify a newly added Transaction
        Block lastBlock = blockchain.get(blockchain.size() - 1);
        System.out.println("Capacity of last block = " + lastBlock.capacity);
        if (lastBlock.capacity > 0)
            lastBlock.addTransaction(pendingToVerify.remove(), label);
        else {
            Block newBlock = new Block(lastBlock.hash, lastBlock.blockNumber + 1);
            blockchain.add(newBlock);
            newBlock.addTransaction(pendingToVerify.remove(), label);
        }
    }
}
