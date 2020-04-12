import java.util.*;
import com.Transact.Transaction;
import com.UI.MainFrame;
import com.utility.*;
import com.wallet.*;

class Block {
    // creates a block
    public String hash;
    public String previousBlockHash;
    public String merkleRoot;
    public ArrayList<Transaction> transactions = new ArrayList<>();
    public ArrayList<Transaction> failedTransactions = new ArrayList<>();
    private int nonce;
    public int blockNumber;
    private long timestamp;

    public Block(String previousBlockHash, int blockNumber) {
        this.previousBlockHash = previousBlockHash;
        this.timestamp = new Date().getTime();
        this.blockNumber = blockNumber;
        this.hash = findHash();
    }

    public String findHash() {
        // calls the method that performs hashing
        String calculatedHash = Util.hashAlgo(previousBlockHash + merkleRoot + timestamp + nonce);
        return calculatedHash;
    }

    public void mineBlock(int difficulty) {
        // mines the block by comparing the number of prefix zeroes in the hash of the block(with the help of a nonce)
        merkleRoot = Util.computeMerkleRoot(transactions);
        String required = new String(new char[difficulty]).replace('\0', '0');
        while(!hash.substring(0, difficulty).equals(required)) {
            nonce++;
            hash = findHash();
        }
        System.out.println("Block " + blockNumber +  " has been mined successfully!! " + hash);
    }

    public boolean addTransaction(Transaction t, User sender, User receiver) {
        // calls other methods that help in validating and then adding the transaction to a block
        if(t == null) {
            return false;
        }
        t.takeSignature(sender.privateKey, receiver.privateKey);
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
            if(t.senderAddress.equals(u.publicKey) || t.receiverAddress.equals(u.publicKey)) {
                s.add(t);
            }
        }
        ArrayList<Transaction> f = new ArrayList<>();
        for(Transaction t : failedTransactions) {
            if(t.senderAddress.equals(u.publicKey) || t.receiverAddress.equals(u.publicKey)) {
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

class Medichain {
    // it contains the main blockchain
    // it is a singleton class
    public ArrayList<Block> blockchain = new ArrayList<>();
    public ArrayList<User> users = new ArrayList<>();
    private static boolean valid = true;
    public static int difficulty = 3;

    private Medichain() {}

    public static Medichain getInstance() {
        if(valid) {
            valid = false;
            return new Medichain();
        }
        else
            throw new RuntimeException();
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
                System.out.println("Block number: " + blockchain.get(i).blockNumber + " is not mined");
                return false;
            }
            for(int j = 0; j < currentBlock.transactions.size(); j++) {
                Transaction current = currentBlock.transactions.get(j);
                if(!current.verifySignature(current.senderAddress, current.receiverAddress)) {
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

public class Driver {
    static void printMenu() {
        // a method to print the menu
        System.out.println("MENU");
        System.out.println("1.Create new user");
        System.out.println("2.Make a new transaction");
        System.out.println("3.Show user transactions");
        System.out.println("4.Mine block");
        System.out.println("5.Validate the blockchain");
        System.out.println("6.Exit");
        System.out.print("Enter your option: ");
    }

    public static void main(String[] args) {
        // Create and display GUI from event dispatching thread (enhances thread safety)
        javax.swing.SwingUtilities.invokeLater(MainFrame::new);

        // driver class which temporarily works on switch cases
        Medichain med = Medichain.getInstance();
        Block block = new Block("0", 1);
        med.addBlock(block);
        String prev = block.hash;
        //this is the genesis block
        block = new Block(prev, 2);
        Scanner sc = new Scanner(System.in);
        int option = 0;
        while(option != 6) {
            printMenu();
            option = sc.nextInt();
            switch(option) {
                case 1 : System.out.print("Enter user name: ");
                         sc.next();
                         String name = sc.nextLine();
                         System.out.print("Enter user age: ");
                         int age = sc.nextInt();
                         System.out.print("Enter user gender: ");
                         sc.next();
                         String gender = sc.nextLine();
                         System.out.print("Enter user category(doctor/patient)");
                         sc.next();
                         String category = sc.nextLine();
                         med.users.add(new User(name, gender, category, age));
                         if(med.users.size() == 1) {
                             med.users.get(med.users.size()-1).userID = 1;
                         }
                         else {
                             med.users.get(med.users.size()-1).userID = med.users.get(med.users.size()-2).userID + 1;
                         }
                         System.out.println("User created successfully");
                         System.out.println("Private key gets stored on the device locally please do not disclose it to anyone");
                         System.out.println("Public Key: " + Util.getKeyValue(med.users.get(med.users.size()-1).publicKey));
                         System.out.println("Private Key: " + Util.getKeyValue(med.users.get(med.users.size()-1).privateKey));
                         System.out.println("Your user id is: " + med.users.get(med.users.size()-1).userID);
                         break;

                case 2 : System.out.print("Enter the userID of sender: ");
                         int sender = sc.nextInt();
                         System.out.print("Enter the userID of receiver: ");
                         int receiver = sc.nextInt();
                         User s = null, r = null;
                         for(User i : med.users) {
                             if(i.userID == sender) {
                                 s = i;
                             }
                             if(i.userID == receiver) {
                                 r = i;
                             }
                         }
                         System.out.println("Enter the medications(type '/' in new line to stop writting): ");
                         ArrayList<String> prescription = new ArrayList<>();
                         while(true) {
                             String str = sc.nextLine();
                             if(!str.equals("/")) {
                                 prescription.add(str);
                             }
                             else
                                 break;
                         }
                         if(s != null && r != null) {
                             Transaction transaction = new Transaction(s.publicKey, r.publicKey, prescription);
                             block.addTransaction(transaction, s, r);
                         }
                         else {
                             System.out.println("Invalid id");
                         }
                         break;

                case 3 : System.out.println("Only the transactions present in the mined blocks will be visible");
                         System.out.print("Enter the user id: ");
                         int id = sc.nextInt();
                         User u = null;
                         for(User i : med.users) {
                             if(i.userID == id) {
                                u = i;
                                break;
                             }
                         }
                         if(u == null) {
                             System.out.println("Invalid user");
                             break;
                         }
                         for(Block i : med.blockchain) {
                             i.showUser(u);
                         }
                         break;

                case 4 : med.addBlock(block);
                         String previousHash = block.hash;
                         block = new Block(previousHash, med.blockchain.size()+1);
                         break;

                case 5 : med.isChainValid();
                         break;

                case 6 : break;

                default: System.out.println("Invalid option");
            }
        }
    }
}
