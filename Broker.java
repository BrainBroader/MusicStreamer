import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;


public class Broker extends Node{

    public List<Consumer> registeredUser;
    public List<Publisher> registeredPublishers;

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











    /*public static void main(String args[]) {
        new Broker().openServer();
    }

    ServerSocket providerSocket;
    Socket connection = null;

    void openServer() {
        try {

            providerSocket = new ServerSocket(4321, 10);
            System.out.println("Server started");
            System.out.println("Waiting for a Publisher ...");

            while (true) {
                connection = providerSocket.accept();
                System.out.println("Publisher connected.");
                ActionForPubs add = new ActionForPubs(connection);
                System.out.println("Handler created.");
                new Thread(add).start();
            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                providerSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }*/
}