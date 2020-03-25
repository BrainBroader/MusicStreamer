import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Publisher extends Node {

    private static final String BROKER_IP = "127.0.0.1";
    private static final int BROKER_PORT = 9090;
    static ServerSocket listener;

    public Publisher() {

    }

    public void getBrokerList() {

    }

    public Broker hashTopic(ArtistName a) {
        return null;
    }

    public void push(ArtistName a, Value v) {

    }

    public void notifyFailure(Broker b) {

    }

    public static void main(String[] args) throws UnknownHostException, IOException {
        Socket socket = new Socket(BROKER_IP, BROKER_PORT);

        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        while (true) {
            System.out.print("> ");
            String command = keyboard.readLine();

            out.println(command);
            String serverResponse = input.readLine();
            System.out.println("BROKER SAYS: " + serverResponse);
        }
    }
}