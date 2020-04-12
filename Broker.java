import java.io.*;
import java.util.*;
import java.net.*;

// Server class
public class Broker extends Thread {

    private static int port;
    private static ArrayList<ActionsForPub> publishers = new ArrayList<>();
    private static ArrayList<ActionsForConsumer> consumers = new ArrayList<>();

    private static ArrayList<Publisher> pub = new ArrayList<>();

    private static ArrayList<String> artists = new ArrayList<>();

    private static ArrayList<Integer> brokers_ports = new ArrayList<>();
    private static ArrayList<String> brokers_ip = new ArrayList<>();

    private static HashMap<String, String> brokers_list = new HashMap<>();
    private static HashMap<ArrayList<String>, String> pub_servers = new HashMap<>();

    public static Queue<MusicFile> queue = new LinkedList<>();



    public static void main(String[] args) throws IOException {

        port = loadPorts("brokers2.txt");

        new Broker().openServer(port);

    }

    public void openServer(int PORT) throws IOException {

        // server is listening on port 5056
        ServerSocket ss = new ServerSocket(PORT);
        System.out.println("Server started.");
        System.out.println("Server's IP: " + getIP() + ", Server's PORT: " + PORT);


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
                    //System.out.println("Assigning new thread for this publisher.");

                    // create a new thread object
                    ActionsForPub t = new ActionsForPub(s, dis, dos, PORT, this, "connect");
                    publishers.add(t);
                    // Invoking the start() method
                    t.start();

                } else if (received.equals("Consumer")) {

                    System.out.println("\nA new consumer connected. : " + s);
                    //System.out.println("Assigning new thread for this consumer.");

                    // create a new thread object
                    ActionsForConsumer t2 = new ActionsForConsumer(s, dis, dos, PORT, this);
                    consumers.add(t2);
                    // Invoking the start() method
                    t2.start();

                } else if (received.equals("reconnect")) {

                    System.out.println("\nA consumer reconnected. : " + s);
                    //System.out.println("Assigning new thread for this consumer.");

                    // create a new thread object
                    ActionsForConsumer t2 = new ActionsForConsumer(s, dis, dos, PORT, this, "reconnect");
                    consumers.add(t2);
                    // Invoking the start() method
                    t2.start();
                } else if (received.equals("PubVol2")) {

                    // create a new thread object
                    ActionsForPub t = new ActionsForPub(s, dis, dos, PORT, this, "reconnect");
                    publishers.add(t);
                    // Invoking the start() method
                    t.start();

                }

            } catch (Exception e) {
                s.close();
                e.printStackTrace();
            }
        }
    }

    public void putBroker_list(String art, String broker) {
        this.brokers_list.put(art, broker);
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

    public String getIP() throws IOException {
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            String IP = socket.getLocalAddress().getHostAddress();
            return IP;
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

    public int getPort() {
        return this.port;
    }

    public HashMap<String, String> getBrokers_list() {
        return this.brokers_list;
    }

    public ArrayList<String> getArtists() {
        return this.artists;
    }

    public void saveArtists(ArrayList<String> list) {

        for (int i = 0; i < list.size(); i++) {
            this.artists.add(list.get(i));
        }
    }

    public void addPublisher(Publisher p) {
        this.pub.add(p);
    }

    public ArrayList<Publisher> getPublishers() {
        return this.pub;
    }

    public Queue<MusicFile> getQueue() {
        return this.queue;
    }

    public void addQueue(MusicFile e) {
        this.queue.add(e);
    }

    public void addPub_servers(ArrayList<String> list, String name) {
        this.pub_servers.put(list,name);
    }

    public HashMap<ArrayList<String>, String> getPub_servers() {
        return this.pub_servers;
    }

    public void setBrokers_list(HashMap<String, String> brokers_list) {
        this.brokers_list = brokers_list;
    }
}