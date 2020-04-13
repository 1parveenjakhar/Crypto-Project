package com.wallet;
import com.utility.Util;

import java.io.Serializable;
import java.security.*;
import java.util.ArrayList;
import java.util.Random;

import static com.utility.CommonConstants.primes;

public class User implements Serializable {
    // this class creates a user
    // user can be a patient or doctor (temporarily not restricted)
    private final String name, gender, category, age;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private final String userID;
    private final boolean isMiner;
    private int secretKey;

    public User(String name, String age, String gender, String category, boolean isMiner, String userID) {
        generateKeys();
        this.name = name;
        this.gender = gender;
        this.category = category;
        this.age = age;
        this.isMiner = isMiner;
        this.userID = userID;
        generateSecretKey();
    }

    private int generator(int a) {
        int order = a-1;
        int ans = -1;
        for(int i = 1; i <= order; i++) {
            int temp = 1;
            while(true) {
                if(Util.power(i, temp, a)%a == 1) {
                    if(temp == order) {
                        ans = i;
                    }
                    break;
                }
                temp++;
            }
            if(ans != -1) {
                break;
            }
        }
        return ans;
    }

    private int getPrimes() {
        int primeNumbers;
        Random rand = new Random();
        primeNumbers = primes[rand.nextInt(20)];
        return primeNumbers;
    }

    public ArrayList<Integer> extractDetails(int b) {
        int p = getPrimes();
        int g = generator(p);
        int y = Util.power(g, secretKey, p);
        Random rand = new Random();
        int r = rand.nextInt(p-1);
        int h = Util.power(g, r, p);
        int s = (r + b * secretKey) % (p-1);
        ArrayList<Integer> details = new ArrayList<>();
        details.add(p);
        details.add(g);
        details.add(s);
        details.add(y);
        details.add(h);
        return details;
    }

    private void generateSecretKey() {
        int difficulty = 3;
        secretKey = 0;
        String pKey = Util.getKeyValue(privateKey);
        String hash = Util.hashAlgo(pKey + secretKey);
        String target = new String(new char[difficulty]).replace('\0', '0');
        while(!hash.substring(0,difficulty).equals(target)) {
            secretKey++;
            hash = Util.hashAlgo(pKey + secretKey);
        }
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

    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}
