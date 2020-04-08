import java.io.IOException;
import java.io.Serializable;
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

    public void lock()throws InterruptedException
    {
        synchronized(this)
        {
            wait();
        }
    }

    public void unlock()throws InterruptedException
    {
        synchronized(this)
        {
            notify();
        }
    }

}