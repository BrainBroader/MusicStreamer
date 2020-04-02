import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class Node extends Thread {

    //List<Broker> brokers;


    public Node(){

    }

    /*public Node(List<Broker> brokers) {

        this.brokers = brokers;
    }*/

    public void init(int i) {

    }

    /*public List<Broker> getBrokers() {

        return this.brokers;
    }*/



    public void connect() {

    }

    public void discconnect() {

    }

    public void updateNode() {

    }

    public static BigInteger MD5(String input)
    {
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            return no;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}