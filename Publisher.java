import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Publisher extends Thread
{
    private static ArrayList<Integer> brokers_ports = new ArrayList<>();
    private static ArrayList<String> brokers_ip = new ArrayList<>();

    private static ArrayList<String> artists = new ArrayList<>();
    private static ArrayList<MusicFile> songs = new ArrayList<>();
    private static ArrayList<String> filenames = new ArrayList<>();

    private static ArrayList<Broker> brokers = new ArrayList<>();
    private static HashMap<BigInteger, String> hash_brokers = new HashMap<>();


    private static ArrayList<BigInteger> h_artists = new ArrayList<>();
    private static HashMap<BigInteger, String> hash_ip = new HashMap<>();
    private static ArrayList<BigInteger> Ipp = new ArrayList<>();
    private static HashMap<BigInteger, String> hash_art = new HashMap<>();
    private static HashMap<String, String> brokers_list = new HashMap<>();

    private static HashMap<ArrayList<String>, String> pub_server = new HashMap<>();

    private static String Ip;
    private static int Port;
    private static char from;
    private static char to;

    public Publisher() {

    }

    public Publisher(int Port, char from, char to) {
        this.Port = Port;
        this.from = from;
        this.to = to;
    }


    public static void main(String[] args) throws Exception {

        System.out.print("Type a folder: ");
        Scanner keyboard = new Scanner(System.in);
        String dpath = keyboard.nextLine();

        System.out.print("From : ");
        String from1 = keyboard.nextLine();

        System.out.print("To : ");
        String to1 = keyboard.nextLine();

        from1 = from1.toLowerCase();
        to1 = to1.toLowerCase();

        char from = from1.charAt(0);
        char to = to1.charAt(0);

        System.out.println();

        String filepath = System.getProperty("user.dir") + "\\" + dpath;
        Path dir = FileSystems.getDefault().getPath(filepath);
        DirectoryStream<Path> stream = Files.newDirectoryStream( dir );
        for (Path path : stream) {

            String p = filepath + "/" + (path.getFileName()).toString();
            Mp3Parse parse = new Mp3Parse();
            MusicFile m = new MusicFile();
            m = parse.mp3extraction(p);

            String name = m.getArtistName();
            if (name != null && name.length() != 0) {
                String name1 = name.toLowerCase();
                char letter = name1.charAt(0);

                if (letter >= from && letter <= to) {
                    songs.add(m);
                    addArtist(m.getArtistName());
                    filenames.add(path.getFileName().toString());
                }
            }
        }
        stream.close();

        System.out.println("Tracks : "+songs.size());

        loadPorts("brokers1.txt");
        int port = loadPorts2("Publishers1.txt");

        Publisher pub = new Publisher(port,from, to);

        String iport = pub.getIP() + " " +Integer.toString(port);
        pub_server.put(artists, iport);

        pub.beginHash();
        System.out.println(brokers_list);

        pub.connect("connect");

        pub.openServer(port);

    }

    public void connect(String job) {

        ArrayList<PubHandler> pub_array = new ArrayList<>();

        for (int i = 0; i < brokers_ip.size(); i++) {
            PubHandler handler = new PubHandler(brokers_ip.get(i), brokers_ports.get(i), this, job);
            pub_array.add(handler);
            handler.start();
        }

        for (int i = 0; i < brokers_ip.size(); i++) {
            try {
                pub_array.get(i).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void openServer(int PORT) throws IOException {

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

                System.out.println("\nA new broker connected. : " + s);

                PubServer server = new PubServer(s, dis, dos, PORT, this);
                server.start();

            } catch (Exception e) {
                s.close();
                e.printStackTrace();
            }
        }
    }

    public void beginHash() throws IOException {

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

    public String getIP() throws IOException {
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            String IP = socket.getLocalAddress().getHostAddress();
            return IP;
        }
    }

    public static void loadPorts(String data) {
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

    public static void addArtist(String artist) {

        if (!artists.contains(artist)) {
            if (artist != null) {
                if (artist.length() != 0) {
                    artists.add(artist);
                }
            }
        }
    }

    public static int loadPorts2(String data) {
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

            FileWriter fileStream = new FileWriter("Publishers1.txt");
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

    public ArrayList<String> getBrokers_ip() {
        return this.brokers_ip;
    }

    public ArrayList<Integer> getBrokers_ports() {
        return this.brokers_ports;
    }

    public ArrayList<String> getArtistsList() {
        return this.artists;
    }

    public ArrayList<String> getFilenames() {
        return this.filenames;
    }

    public ArrayList<MusicFile> getSongs() {
        return this.songs;
    }


    public void setHash_brokers(HashMap<BigInteger, String> hash_brokers) {
        this.hash_brokers = hash_brokers;
    }

    public ArrayList<Broker> getBrokers() {
        return this.brokers;
    }

    public void addBrokers(Broker broker) {
        this.brokers.add(broker);
    }

    public HashMap<String, String> getBrokers_list() {
        return this.brokers_list;
    }

    public int getPort() {
        return this.Port;
    }

    public char getFrom() {
        return this.from;
    }

    public char getTo() {
        return this.to;
    }

    public HashMap<ArrayList<String>, String> getPub_server() {
        return this.pub_server;
    }

    public void setBrokers_ports(ArrayList<Integer> brokers_ports) {
        this.brokers_ports = brokers_ports;
    }

    public void setBrokers_ip(ArrayList<String> brokers_ip) {
        this.brokers_ip = brokers_ip;
    }

    public void setArtists(ArrayList<String> artists) {
        this.artists = artists;
    }

    public void setH_artists(ArrayList<BigInteger> h_artists) {
        this.h_artists = h_artists;
    }

    public void setHash_ip(HashMap<BigInteger, String> hash_ip) {
        this.hash_ip = hash_ip;
    }

    public void setIpp(ArrayList<BigInteger> ipp) {
        this.Ipp = ipp;
    }

    public void setHash_art(HashMap<BigInteger, String> hash_art) {
        this.hash_art = hash_art;
    }
}
