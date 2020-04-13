package com;

import com.Transact.Transaction;
import com.wallet.User;

import java.io.Serializable;
import java.util.ArrayList;

public class MedicalChain implements Serializable {
    // it contains the main blockchain
    // it is a singleton class
    public ArrayList<Block> blockchain;
    public ArrayList<User> users;
    private static boolean valid = true;
    public static int difficulty = 3;
    private static MedicalChain instance;

    private MedicalChain() {
        blockchain = new ArrayList<>();
        users = new ArrayList<>();
        instance = this;
    }

    public static MedicalChain getInstance() throws Exception {
        if (instance == null)
            return new MedicalChain();
        if (!valid)
            throw new Exception("<html>Your BlockChain is not valid!<br/>Probably, it is tampered!</html>");
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
                return false;
            }
            if(!currentBlock.hash.equals(currentBlock.findHash())) {
                System.out.println("Current hashes not equal");
                return false;
            }
            if(!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
                System.out.println("com.Block number: " + blockchain.get(i).blockNumber + " is not mined");
                return false;
            }
            for(int j = 0; j < currentBlock.transactions.size(); j++) {
                Transaction current = currentBlock.transactions.get(j);
                if(current.verifySignature(current.getSenderAddress(), current.getReceiverAddress())) {
                    System.out.println("Signature on Transaction: " + j + " is invalid");
                    return false;
                }
            }
        }
        System.out.println("Blockchain is valid");
        return true;
    }

    public void addBlock(Block block) {
        // adds the block to the blockchain
        block.mineBlock(difficulty);
        blockchain.add(block);
    }
}
