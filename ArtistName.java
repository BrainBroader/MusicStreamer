import java.io.*;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class ArtistName {

    private String artistName;

    public ArtistName() { }

    public ArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setName(String artistName) {
        this.artistName = artistName;
    }

    public String getName() {
        return this.artistName;
    }

    public String toString() {
        return "Artist: " + getName();
    }

/////////////////////////////////////////////////////////////////////////////////
    /*private static int port;
    private static BigInteger iportName;
    private static ArrayList<ActionsForPub> publishers = new ArrayList<ActionsForPub>();
    private static ArrayList<ActionsForConsumer> consumers = new ArrayList<ActionsForConsumer>();
    private static ArrayList<Integer> brokers_ports = new ArrayList<Integer>();
    private static ArrayList<String> brokers_ip = new ArrayList<String>();
    private static ArrayList<String> artists = new ArrayList<String>();
    private static ArrayList<BigInteger> Ipp = new ArrayList<>();  //hash(ip+port)
    private static ArrayList<BigInteger> h_artists = new ArrayList<>();  //hash(artistName)
    private static HashMap<BigInteger, String> hash_ip = new HashMap<>();   //hash(ip+port), ip + port
    private static HashMap<BigInteger, String> hash_art = new HashMap<>();  //hash(artistName), artistName
    private static HashMap<String, String> brokers_list = new HashMap<>();  //artistName, ip+port (of Broker that artistName belongs to)


    public static void main(String[] args) throws IOException {

//        artists.add("Komiku");
        artists.add("Kevin MacLeod");
//        artists.add("dogsounds");
//        artists.add("Severed Personality");
        artists.add("Rafael Krux");
        artists.add("Alexander Nakarada");
//        artists.add("Brian Boyko");
        artists.add("Anonymous for Good Reasons");
//        artists.add("Arthur Fordsworthy");
//        artists.add("Phase Shift");
//        artists.add("James Anderson");
//        artists.add("Jason Shaw");
//        artists.add("Orchestralis");
//        artists.add("Brett VanDonsel");

        loadPorts("brokers1.txt");

        ArtistName n = new ArtistName();
        n.beginHash();

        System.out.println(Ipp);
        System.out.println(h_artists);
        System.out.println(brokers_list);

    }

    //117105220339880669808091666773726441880
    //127231205414751577198499048036890031811
    //148804222247184463820020369817303326647
    //293286386503807150019357550430202217512

    // 34397021934442669997552120745861112699
    // 77704980896376274545301349100076820230
    //302992753916800672941179742186654842240

    public void beginHash() throws IOException {

        iportName = MD5(getIP() + Integer.toString(port));

        for (int i=0;i < brokers_ip.size();i++) {
            Ipp.add(MD5(brokers_ip.get(i) + Integer.toString(brokers_ports.get(i))));
            System.out.println(brokers_ip.get(i) + Integer.toString(brokers_ports.get(i)));
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
                System.out.println("ekaaa");
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

    //getting the ip of the broker
    public String getIP() throws IOException {
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            String IP = socket.getLocalAddress().getHostAddress();
            return IP;
        }
    }*/
}