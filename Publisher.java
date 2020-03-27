import java.io.*; 
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
  
// Client class 
public class Publisher extends Node
{
    private static ArrayList<Integer> brokers = new ArrayList<Integer>();
    private static String IP;
    //private static int Brokers;

    public static void main(String[] args) throws IOException {

        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            IP = socket.getLocalAddress().getHostAddress();
        }

        Publisher pub = new Publisher();
        pub.connect();
    }

    public void connect() {
        brokers = loadPorts("brokers1.txt");

        for (int i = 0; i < brokers.size(); i++) {
            PubHandler handler = new PubHandler(IP, brokers.get(i));
            handler.start();
        }
    }

    public static ArrayList<Integer> loadPorts(String data) {
        ArrayList<Integer> array = new ArrayList<Integer>();
        File f = null;
        BufferedReader reader = null;
        String line;

        try {
            f = new File(data);
        } catch (NullPointerException e) {
            System.err.println("File not found.");

        } try {
            reader = new BufferedReader(new FileReader(f));
        } catch (FileNotFoundException e) {
            System.err.println("Error opening file!");

        } try {
            line = reader.readLine();
            while(line != null){

                String[] splited = line.split("\\s+");
                int port = Integer.parseInt(splited[0]);
                array.add(port);

                line = reader.readLine();
            }
        } catch (IOException e) {
            System.out.println("Error!!!");
        }
        return array;
    }
} 