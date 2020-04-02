import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.net.*;

// Server class
public class Broker extends Node {

    private static int port;
    private static BigInteger iportName;
    private static ArrayList<ActionsForPub> publishers = new ArrayList<ActionsForPub>();
    private static ArrayList<ActionsForConsumer> consumers = new ArrayList<ActionsForConsumer>();
    private static ArrayList<Integer> brokers_ports = new ArrayList<Integer>();
    private static ArrayList<String> brokers_ip = new ArrayList<String>();
    private static ArrayList<String> artists = new ArrayList<String>();
    private static ArrayList<BigInteger> Ipp = new ArrayList<>();
    private static ArrayList<BigInteger> h_artists = new ArrayList<>();
    private static Queue<String> conTopub = new LinkedList<>();
    private static Queue<String> pubTocon = new LinkedList<>();
    private boolean flag = false;
    private boolean flag2 = false;


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
                    Thread t2 = new ActionsForConsumer(s, dis, dos, PORT, this);
                    consumers.add((ActionsForConsumer) t2);
                    // Invoking the start() method
                    t2.start();

                } else if (received.equals("reconnect")) {

                    System.out.println("\nA new consumer connected. : " + s);
                    System.out.println("Assigning new thread for this consumer.");

                    // create a new thread object
                    Thread t2 = new ActionsForConsumer(s, dis, dos, PORT, this, "reconnect");
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

    HashMap<BigInteger, String> hash_ip = new HashMap<>();
    HashMap<BigInteger, String> hash_art = new HashMap<>();
    HashMap<String, String> brokers_list = new HashMap<>();

    public void beginHash() throws IOException {

        iportName = MD5(getIP() + Integer.toString(port));

        for (int i=0;i < brokers_ip.size();i++) {
            Ipp.add(MD5(brokers_ip.get(i) + Integer.toString(brokers_ports.get(i))));
            hash_ip.put(MD5(brokers_ip.get(i) + Integer.toString(brokers_ports.get(i))), brokers_ip.get(i) + " " +Integer.toString(brokers_ports.get(i)));
        }

        Collections.sort(Ipp);

        for (int j=0; j < artists.size(); j++) {
            h_artists.add(MD5(artists.get(j)));
            hash_art.put(MD5(artists.get(j)), artists.get(j));
        }

        Collections.sort(h_artists);



        for (int i = 0; i < h_artists.size(); i++) {
            BigInteger broker = findBroker(h_artists.get(i));

            String artist = hash_art.get(h_artists.get(i));
            String br = hash_ip.get(broker);

            brokers_list.put(artist, br);
        }
        //System.out.println(brokers_list);
    }


    //Finds in which Broker this
    public BigInteger findBroker(BigInteger artist) {

        BigInteger big = BigInteger.valueOf(0);


        for (int j = (Ipp.size() - 1); j >= 0; j--) {
            if ((j == (Ipp.size() - 1)) && (artist.compareTo(Ipp.get(j)) > 0)) {
                 artist = artist.mod(Ipp.get(j));
            }

            if (j != 0) {
                if ((artist.compareTo(Ipp.get(j)) < 0) && (artist.compareTo(Ipp.get(j - 1)) > 0)) {
                    big = Ipp.get(j);
                    return big;
                }
            } else {
                if (artist.compareTo(Ipp.get(j)) < 0) {
                    big = Ipp.get(j);
                    return big;
                }
            }
        }
        return big;
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

    // Prints a string and waits for consume()
    public void send()throws InterruptedException
    {
        // synchronized block ensures only one thread
        // running at a time.
        synchronized(this)
        {
            //System.out.println("producer thread running");

            // releases the lock on shared resource
            wait();

            // and waits till some other method invokes notify().
            //System.out.println("Resumed");
        }
    }

    // Sleeps for some time and waits for a key press. After key
    // is pressed, it notifies produce().
    public void arrive()throws InterruptedException
    {
        // this makes the produce thread to run first.
        //Thread.sleep(1000);
        //Scanner s = new Scanner(System.in);

        // synchronized block ensures only one thread
        // running at a time.
        synchronized(this)
        {
            //System.out.println("Waiting for return key.");
            //s.nextLine();
            //System.out.println("Return key pressed");

            // notifies the produce thread that it
            // can wake up.
            notify();

            // Sleep
            //Thread.sleep(2000);
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

    public int getPort() {
        return this.port;
    }

    public ArrayList<BigInteger> getIpp() {
        return this.Ipp;
    }

    public HashMap<String, String> getBrokers_list() {
        return this.brokers_list;
    }

    public Queue<String> getConTopub() {
        return this.conTopub;
    }

    public Queue<String> getPubTocon() {
        return this.pubTocon;
    }

    public boolean getFlag() {
        return this.flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }


    public boolean getFlag2() {
        return this.flag2;
    }

    public void setFlag2(boolean flag2) {
        this.flag2 = flag2;
    }



    public static void main(String[] args) throws IOException {

        port = loadPorts("brokers2.txt");

        new Broker().openServer(port);

    }
}