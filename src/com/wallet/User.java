package com.wallet;
import java.security.*;

public class User {
    // this class creates a user
    // user can be a patient or doctor (temporarily not restricted)
    private String name, gender, category;
    private int age;
    public PrivateKey privateKey;
    public PublicKey publicKey;
    public int userID;

    public User(String name, String gender, String category, int age) {
        generateKeys();
        this.name = name;
        this.gender = gender;
        this.category = category;
        this.age = age;
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
}
