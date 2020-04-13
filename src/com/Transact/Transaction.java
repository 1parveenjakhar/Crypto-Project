package com.Transact;import com.utility.Util;import com.wallet.User;import java.io.Serializable;import java.security.*;import java.util.ArrayList;import java.util.Date;import java.util.Random;public class Transaction implements Serializable {    // this class records the details of transactions    private final PublicKey senderAddress;    private final PublicKey receiverAddress;    private final String description;    private String prescriptionID;    private final ArrayList<String> medicines;    private byte[] signature1, signature2;    private static int count = 0;    private final long timeStamp;    public Transaction(PublicKey from, PublicKey to, String text, ArrayList<String> med) {        senderAddress = from;        receiverAddress = to;        medicines = med;        description = text;        timeStamp = new Date().getTime();    }    private String calculateHash() {        // calls method for doing hashing        count++;        return Util.hashAlgo(Util.getKeyValue(senderAddress) + Util.getKeyValue(receiverAddress) + count + timeStamp + description + getPrescritionValue());    }    private String getPrescritionValue() {        //converts arrayList to a single string        StringBuilder s = new StringBuilder();        for(String i : medicines) {            s.append(i);        }        return s.toString();    }    public void takeSignature(PrivateKey doctor, PrivateKey patient) {        //takes the private key of sender and receiver        //implementation has to be changed a bit        String data = Util.getKeyValue(senderAddress) + Util.getKeyValue(receiverAddress) + timeStamp + description + medicines;        String data1 = Util.getKeyValue(receiverAddress) + Util.getKeyValue(senderAddress) + timeStamp + description + medicines;        signature1 = Util.applySig(doctor, data);        signature2 = Util.applySig(patient, data1);    }    public boolean verifySignature(PublicKey doctor, PublicKey patient) {        // verifies the signature with the help of helper methods        String data = Util.getKeyValue(doctor) + Util.getKeyValue(receiverAddress) + timeStamp + description + medicines;        String data1 = Util.getKeyValue(patient) + Util.getKeyValue(senderAddress) + timeStamp + description + medicines;        return Util.verifySig(doctor, data, signature1) || Util.verifySig(patient, data1, signature2);    }    public boolean processTransaction() {        // aids in validating the transaction and adding it to the block        if(verifySignature(senderAddress, receiverAddress)) {            System.out.println("Transaction false");            return false;        }        if(medicines == null) {            System.out.println("No change in report : transaction invalid");            return false;        }        prescriptionID = calculateHash();        return true;    }    public boolean zeroKnowledgeProofFailure(User u) {        Random rand = new Random();        int b = rand.nextInt(2);        ArrayList<Integer> details = u.extractDetails(b);        int p = details.get(0);        int g = details.get(1);        int s = details.get(2);        int y = details.get(3);        int h = details.get(4);        int x = Util.power(g, s, p);        y = h * (int)Math.pow(y, b) % p;        return x != y;    }    public String getPrescriptionID() {        return prescriptionID;    }    public PublicKey getSenderAddress() {        return senderAddress;    }    public PublicKey getReceiverAddress() {        return receiverAddress;    }    public String getDescription() {        return description;    }    public ArrayList<String> getMedicines() {        return medicines;    }}