import java.io.*; 
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
  
// Client class 
public class Publisher extends Node
{
    private static ArrayList<Integer> brokers_ports = new ArrayList<Integer>();
    private static ArrayList<String> brokers_ip = new ArrayList<String>();
    private static String IP;
    //private static int Brokers;

    public static void main(String[] args) throws IOException {

        /*try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            IP = socket.getLocalAddress().getHostAddress();
        }*/

        Publisher pub = new Publisher();
        pub.connect();
    }

    public void connect() {
        loadPorts("brokers1.txt");

        /*for(int j = 0; j < brokers_ip.size(); j++ ) {
            System.out.println(brokers_ip.get(j));
        }

        for(int j = 0; j < brokers_ports.size(); j++ ) {
            System.out.println(brokers_ports.get(j));
        }*/

        for (int i = 0; i < brokers_ip.size(); i++) {
            PubHandler handler = new PubHandler(brokers_ip.get(i), brokers_ports.get(i));
            handler.start();
        }
    }

    public static void loadPorts(String data) {
        //ArrayList<Integer> array = new ArrayList<Integer>();
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
                String ip = splited[0];
                int port = Integer.parseInt(splited[1]);
                brokers_ports.add(port);
                brokers_ip.add(ip);

                line = reader.readLine();
            }
        } catch (IOException e) {
            System.out.println("Error!!!");
        }
    }
} 