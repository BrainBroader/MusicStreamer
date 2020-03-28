import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;
  
// Client class 
public class Publisher extends Node
{
    private static ArrayList<Integer> brokers_ports = new ArrayList<Integer>();
    private static ArrayList<String> brokers_ip = new ArrayList<String>();
    private ArrayList<MusicFile> SongList = new ArrayList<MusicFile>();
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
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Creates an artistName list
    ArrayList<ArtistName> ArtistList = new ArrayList<>();
    public void FindArtists (String name) {
        boolean flag = true;
        for (int i = 0; i < ArtistList.size(); i++) {
            if (name.equals(ArtistList.get(i))) {
                flag = false;
                break;
            }
        }
        if (flag) {
            ArtistName hexName = new ArtistName(name);
            ArtistList.add(hexName);
        }
    }

    ArrayList<BigInteger> Ipp = new ArrayList<>();
    //Hashing the artist name and return a Broker instance
    public Broker HashTopic(ArtistName name){
        BigInteger hexName = MD5(name.getName());
        for(int i=0;i < brokers_ip.size();i++){
            Ipp.add(MD5(brokers_ip.get(i) + Integer.toString(brokers_ports.get(i))));
        }
        Broker Br = FindBroker(hexName);
        return Br;

    }

    //Finds in which Broker this
    public Broker FindBroker(BigInteger hexName) {
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
        }else return null;

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

} 