import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

// Client class 
public class Publisher extends Node
{
    private static ArrayList<Broker> brokers = new ArrayList<>();
    private static ArrayList<Integer> brokers_ports = new ArrayList<>();
    private static ArrayList<String> brokers_ip = new ArrayList<>();
    private static HashMap<BigInteger, String> hash_brokers = new HashMap<>();
    private static ArrayList<String> artists = new ArrayList<>();
    private static ArrayList<MusicFile> songs = new ArrayList<>();
    private static HashMap<String, String> broker_list = new HashMap<>();
    private static ArrayList<String> filenames = new ArrayList<>();
    private static int publishers;
    private static String IP;
    //private static int Brokers;

    public static void main(String[] args) throws Exception {


        String filepath = System.getProperty("user.dir") + "\\dataset1\\Comedy";
        Path dir = FileSystems.getDefault().getPath(filepath);
        DirectoryStream<Path> stream = Files.newDirectoryStream( dir );
        for (Path path : stream) {

            String p = filepath + "/" +(path.getFileName()).toString();
            Mp3Parse parse = new Mp3Parse();
            MusicFile m = new MusicFile();
            m = parse.mp3extraction(p);
            songs.add(m);
            addArtist(m.getArtistName());
            filenames.add(path.getFileName().toString());
            //System.out.println(path.getFileName()+" :"+m.getArtistName());

            /*List<MusicFile> list = new ArrayList<MusicFile>();
            list = parse.chunks(m.getMusicFileExtract(), m);
            songs.add(list);*/
        }
        stream.close();

        //System.out.println("Tracks : "+songs.size());

        /*for (int i = 0; i < artists.size(); i++) {
            BigInteger hash = MD5(artists.get(i));
            System.out.println(hash);
        }*/
        loadPorts("brokers1.txt");
        Publisher pub = new Publisher();
        pub.connect();


        int port = loadPorts2("Publishers1.txt");

        pub.openServer(port);

    }

    public void connect() {

        ArrayList<PubHandler> pub_array = new ArrayList<>();

        for (int i = 0; i < brokers_ip.size(); i++) {
            PubHandler handler = new PubHandler(brokers_ip.get(i), brokers_ports.get(i), this);
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

        // server is listening on port 5056
        ServerSocket ss = new ServerSocket(PORT);
        System.out.println("Server started.");
        System.out.println("Waiting for a connection...");


        while (true) {
            Socket s = null;

            try {
                // socket object to receive incoming client requests
                s = ss.accept();

                // obtaining input and out streams
                ObjectOutputStream dos = new ObjectOutputStream(s.getOutputStream());
                ObjectInputStream dis = new ObjectInputStream(s.getInputStream());

                System.out.println("\nA new broker connected. : " + s);

                String artist = (String) dis.readObject();
                System.out.println(artist);


            } catch (Exception e) {
                s.close();
                e.printStackTrace();
            }
        }
    }

    public Broker hashTpoic(String artistName) {

        return null;
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
                artists.add(artist);
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

    public ArrayList<String> getArtitsList() {
        return this.artists;
    }

    public ArrayList<String> getBrokers_ip() {
        return this.brokers_ip;
    }

    public ArrayList<Integer> getBrokers_ports() {
        return this.brokers_ports;
    }

    public void setBroker_list(HashMap<String, String> broker_list) {
        this.broker_list = broker_list;
    }

    public ArrayList<String> getFilenames() {
        return this.filenames;
    }

    public ArrayList<MusicFile> getSongs() {
        return this.songs;
    }

    public void incrPub() {
         this.publishers++;
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
} 