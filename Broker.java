import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Broker extends Node{

    public List<Consumer> registeredUser;
    public List<Publisher> registeredPublishers;

    private static ArrayList<PubHandler> publishers = new ArrayList<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(2);

    private static final int PORT = 9090;

    public Broker() {

    }

    public void calculateKeys() {

    }

    public Publisher acceptConnection(Publisher p ) {
        return null;
    }

    public Consumer acceptConnection(Consumer c) {
        return null;
    }

    public void notifyPublisher(String s) {

    }

    public void pull(ArtistName a) {

    }

    public static void main(String[] args) throws IOException {
        ServerSocket listener = new ServerSocket(PORT);
        while (true) {
            System.out.println("[BROKER] Waiting for publisher to connect...");
            Socket pub =  listener.accept();
            System.out.println("[BROKER] Connected to publisher");

            PubHandler pubThread = new PubHandler(pub);
            publishers.add(pubThread);

            pool.execute(pubThread);
        }
    }
}