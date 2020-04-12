package com.wallet;
import java.io.Serializable;
import java.security.*;

public class User implements Serializable {
    // this class creates a user
    // user can be a patient or doctor (temporarily not restricted)
    private final String name, gender, category, age;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private String userID;
    private boolean isMiner;

    public User(String name, String gender, String category, String age, boolean isMiner, String userID) {
        generateKeys();
        this.name = name;
        this.gender = gender;
        this.category = category;
        this.age = age;
        this.isMiner = isMiner;
        this.userID = userID;
    }

    public void generateKeys() {
        //generates public and private keys for the user
        try {
            KeyPairGenerator key = KeyPairGenerator.getInstance("DSA");
            key.initialize(2048);
            KeyPair pairedKeys = key.generateKeyPair();
            privateKey = pairedKeys.getPrivate();
            publicKey = pairedKeys.getPublic();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public String[] getUserDetails() {
        return new String[]{name, age, gender, category};
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public String getUserID() {
        return userID;
    }

    public boolean isMiner() {
        return isMiner;
    }

    public void setMiner(boolean miner) {
        isMiner = miner;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}
