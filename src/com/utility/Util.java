package com.utility;

import com.Transact.Transaction;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.ArrayList;
import java.util.Base64;

public class Util {
    //this is a utility class that has helper methods
    public static String hashAlgo(String s) {
        //applies hashing on the given input
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                String t = Integer.toHexString(0xff & b);
                if (t.length() == 1)
                    hex.append('0');
                hex.append(t);
            }
            return hex.toString();
        }
        catch(Exception e) {
            throw new RuntimeException();
        }
    }

    public static byte[] applySig(PrivateKey key, String input) {
        // applies signature using DSA
        Signature sig;
        byte[] out = new byte[2];
        try {
            sig = Signature.getInstance("SHA256withDSA");
            sig.initSign(key);
            byte[] strByte = input.getBytes();
            sig.update(strByte);
            out = sig.sign();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return out;
    }

    public static boolean verifySig(PublicKey key, String data, byte[] signature) {
        //verifies the signature using DSA
        boolean result = false;
        try {
            Signature sig = Signature.getInstance("SHA256withDSA");
            sig.initVerify(key);
            sig.update(data.getBytes());
            result = sig.verify(signature);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getKeyValue(Key key) {
        // converts key to string
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static String computeMerkleRoot(ArrayList<Transaction> transacts) {
        // finds merkle root of the transactions present in a single block
        int size = transacts.size();
        ArrayList<String> previousLayer = new ArrayList<>();
        for(Transaction t : transacts) {
            previousLayer.add(t.prescriptionID);
        }
        ArrayList<String> layer = previousLayer;
        while(size > 1) {
            layer = new ArrayList<>();
            for(int i = 1; i < previousLayer.size(); i++) {
                layer.add(hashAlgo(previousLayer.get(i-1) + previousLayer.get(i)));
            }
            size = layer.size();
            previousLayer = layer;
        }
        String merkleRoot;
        if(layer.size() == 1) {
            merkleRoot = layer.get(0);
        }
        else {
            merkleRoot = "";
        }
        return merkleRoot;
    }
}