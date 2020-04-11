package com.Transact;
import com.utility.Util;

import java.security.*;
import java.util.ArrayList;

public class Transaction {
    // this class records the details of transactions
    public PublicKey senderAddress;
    public PublicKey receiverAddress;
    public String prescriptionID;
    public ArrayList<String> prescription;
    public byte[] signature1, signature2;

    private static int count = 0;

    public Transaction(PublicKey from, PublicKey to, ArrayList<String> med) {
        this.senderAddress = from;
        this.receiverAddress = to;
        this.prescription = med;
    }

    private String calculateHash() {
        // calls method for doing hashing
        count++;
        return Util.hashAlgo(Util.getKeyValue(senderAddress) + Util.getKeyValue(receiverAddress) + count + getPrescritionValue());
    }

    private String getPrescritionValue() {
        //converts arrayList to a single string
        String s = "";
        for(String i : prescription) {
            s = s + i;
        }
        return s;
    }

    public void takeSignature(PrivateKey doctor, PrivateKey patient) {
        //takes the private key of sender and receiver
        //implementation has to be changed a bit
        String data = Util.getKeyValue(senderAddress) + Util.getKeyValue(receiverAddress) + prescription;
        String data1 = Util.getKeyValue(receiverAddress) + Util.getKeyValue(senderAddress) + prescription;
        signature1 = Util.applySig(doctor, data);
        signature2 = Util.applySig(patient, data1);
    }

    public boolean verifySignature(PublicKey doctor, PublicKey patient) {
        // verifies the signature with the help of helper methods
        String data = Util.getKeyValue(doctor) + Util.getKeyValue(receiverAddress) + prescription;
        String data1 = Util.getKeyValue(patient) + Util.getKeyValue(senderAddress) + prescription;
        return Util.verifySig(doctor, data, signature1) && Util.verifySig(patient, data1, signature2);
    }

    public boolean processTransaction() {
        // aids in validating the transaction and adding it to the block
        if(!verifySignature(senderAddress, receiverAddress)) {
            System.out.println("Transaction false");
            return false;
        }
        if(prescription == null) {
            System.out.println("No change in report : transaction invalid");
            return false;
        }
        prescriptionID = calculateHash();
        return true;
    }
}
