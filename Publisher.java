import java.io.*; 
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
  
// Client class 
public class Publisher extends Node
{
    private static final ArrayList<Integer> brokers = new ArrayList<Integer>();
    private static String IP;
    private static int Brokers;

    public static void main(String[] args) throws IOException {


        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

        String command = "";
        System.out.print("How many brokers? : ");
        command = keyboard.readLine();
        Brokers = Integer.parseInt(command);
        System.out.println();

        /*for (int i = 0; i < brokers; i++) {
            new Publisher().start();
        }

        System.out.print("Enter Port: ");

        command = keyboard.readLine();
        int PORT = Integer.parseInt(command);
        System.out.println();

        System.out.print("Enter IP: ");
        command = keyboard.readLine();
        String IP = "192.168.1.13";
        System.out.println();*/

        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            IP = socket.getLocalAddress().getHostAddress();
        }



        Publisher pub = new Publisher();
        pub.connect();
    }

    public void connect() {
        brokers.add(5056);
        brokers.add(5057);
        brokers.add(5058);

        for (int i = 0; i < Brokers; i++) {
            PubHandler handler = new PubHandler(IP, brokers.get(i));
            handler.start();
            try {
                handler.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
} 