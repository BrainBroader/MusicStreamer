import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.net.*;

// Server class
public class Broker extends Node {
    private static ArrayList<ActionsForPub> publishers = new ArrayList<ActionsForPub>();
    private static ArrayList<ActionsForConsumer> consumers = new ArrayList<ActionsForConsumer>();
    private static ArrayList<Integer> brokers_ports = new ArrayList<Integer>();
    private static ArrayList<String> brokers_ip = new ArrayList<String>();
    private static ArrayList<String> artists = new ArrayList<String>();
    private static ArrayList<BigInteger> Ipp = new ArrayList<>();


    public void openServer(int PORT) throws IOException {

        // server is listening on port 5056
        ServerSocket ss = new ServerSocket(PORT);
        System.out.println("Server started.");
        System.out.println("Waiting for a connection...");

        // running infinite loop for getting
        // client request
        while (true) {
            Socket s = null;

            try {
                // socket object to receive incoming client requests
                s = ss.accept();


                // obtaining input and out streams
                ObjectOutputStream dos = new ObjectOutputStream(s.getOutputStream());
                ObjectInputStream dis = new ObjectInputStream(s.getInputStream());



                String received = (String)dis.readObject();


                if (received.equals("Publisher")) {
                    System.out.println("\nA new publisher connected. : " + s);
                    System.out.println("Assigning new thread for this publisher.");

                    // create a new thread object
                    Thread t = new ActionsForPub(s, dis, dos, PORT, this);
                    publishers.add((ActionsForPub) t);
                    // Invoking the start() method
                    t.start();

                } else if (received.equals("Consumer")) {

                    System.out.println("\nA new consumer connected. : " + s);
                    System.out.println("Assigning new thread for this consumer.");

                    // create a new thread object
                    Thread t2 = new ActionsForConsumer(s, dis, dos, PORT);
                    consumers.add((ActionsForConsumer) t2);
                    // Invoking the start() method
                    t2.start();

                }

            } catch (Exception e) {
                s.close();
                e.printStackTrace();
            }
        }
    }

    public static int loadPorts(String data) {
        int port = -1;
        File f = null;
        BufferedReader reader = null;
        String line;

        try {
            f = new File(data);
        } catch (NullPointerException e) {
            System.err.println("File not found.");

        }
        try {
            reader = new BufferedReader(new FileReader(f));
        } catch (FileNotFoundException e) {
            System.err.println("Error opening file!");

        }
        try {
            line = reader.readLine();

            String[] splited = line.split("\\s+");
            port = Integer.parseInt(splited[0]);


            Scanner fileScanner = new Scanner(f);
            String firstline = fileScanner.nextLine();

            FileWriter fileStream = new FileWriter("brokers2.txt");
            BufferedWriter out = new BufferedWriter(fileStream);
            while (fileScanner.hasNextLine()) {
                String next = fileScanner.nextLine();
                if (next.equals("\n")) {
                    out.newLine();

                } else {
                    out.write(next);
                    out.newLine();
                }
            }
            out.write(firstline);
            out.close();


        } catch (IOException e) {
            System.out.println("Error!!!");
        }
        return port;
    }

    //getting the ip of the broker
    public String getIP() throws IOException {
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            String IP = socket.getLocalAddress().getHostAddress();
            return IP;
        }
    }

    //Hashing the artist name and return a Broker instance
    public Broker hashTopic(String name) {
        BigInteger hexName = MD5(name);
        for(int i=0;i < brokers_ip.size();i++){
            Ipp.add(MD5(brokers_ip.get(i) + Integer.toString(brokers_ports.get(i))));
        }
        Broker Br = findBroker(hexName);
        return Br;

    }

    //Finds in which Broker this
    public Broker findBroker(BigInteger hexName) {
        if (Ipp.size() > 0) {
            if (hexName.compareTo(Ipp.get(0)) < 0) {
                //this needs to create a thread of broker 1
                Broker br1 = new Broker();
                return br1;
            } else if (hexName.compareTo(Ipp.get(1)) < 0) {
                //this needs to create a thread of broker 2
                Broker br2 = new Broker();
                return br2;
            } else if (hexName.compareTo(Ipp.get(2)) < 0) {
                //this needs to create a thread of broker 3
                Broker br3 = new Broker();
                return br3;
            } else {
                BigInteger val = hexName.mod(Ipp.get(2));
                if (val.compareTo(Ipp.get(0)) < 0) {
                    //this needs to create a thread of broker 1
                    Broker br1 = new Broker();
                    return br1;
                } else if (val.compareTo(Ipp.get(1)) < 0) {
                    //this needs to create a thread of broker 2
                    Broker br2 = new Broker();
                    return br2;
                } else {
                    //this needs to create a thread of broker 3
                    Broker br3 = new Broker();
                    return br3;


                }

            }
        } else {
            return null;
        }

    }

    //hash Algorithm
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

    public void setBrokers_ports(ArrayList<Integer> brokers_ports) {
        this.brokers_ports = brokers_ports;
    }

    public ArrayList<Integer> getBrokers_ports() {
        return this.brokers_ports;
    }

    public void setBrokers_ip(ArrayList<String> brokers_ip) {
        this.brokers_ip = brokers_ip;
    }

    public ArrayList<String> getBrokers_ip() {
        return this.brokers_ip;
    }

    public void setArtists(ArrayList<String> artists) {
        this.artists = artists;
    }

    public ArrayList<String> getArtists() {
        return this.artists;
    }


    public static void main(String[] args) throws IOException {

        int PORT = loadPorts("brokers2.txt");

        new Broker().openServer(PORT);

    }
}