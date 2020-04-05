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

// Client class 
public class Publisher extends Node
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

        System.out.print("From : ");
        Scanner keyboard = new Scanner(System.in);
        String from1 = keyboard.nextLine();

        System.out.print("To : ");
        String to1 = keyboard.nextLine();
        System.out.println();
        from1 = from1.toLowerCase();
        to1 = to1.toLowerCase();

        char from = from1.charAt(0);
        char to = to1.charAt(0);

        String filepath = System.getProperty("user.dir") + "\\dataset1\\Comedy";
        Path dir = FileSystems.getDefault().getPath(filepath);
        DirectoryStream<Path> stream = Files.newDirectoryStream( dir );
        for (Path path : stream) {
            String name = (path.getFileName().toString());
            name = name.toLowerCase();
            char letter = name.charAt(0);

            if(letter >= from && letter <= to) {
                String p = filepath + "/" + (path.getFileName()).toString();
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
        }
        stream.close();

        System.out.println("Tracks : "+songs.size());

        loadPorts("brokers1.txt");
        int port = loadPorts2("Publishers1.txt");

        Publisher pub = new Publisher(port,from, to);

        pub.beginHash();
        System.out.println(brokers_list);

        pub.connect();

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

                String answer = (String) dis.readObject();

                if (answer.equals("Artist")) {
                    String artist = (String) dis.readObject();
                    //System.out.println(artist);

                    ArrayList<String> art_fnames = new ArrayList<>();

                    for (int i = 0; i < songs.size(); i++) {
                        if (artist.equals(songs.get(i).getArtistName())) {
                            art_fnames.add(filenames.get(i));
                        }
                    }

                    dos.writeObject(art_fnames);

                } else if (answer.equals("Song")) {

                    String song_name = (String) dis.readObject();
                    push(song_name, dis, dos);

                }

            } catch (Exception e) {
                s.close();
                e.printStackTrace();
            }
        }
    }

    public void push(String song_name, ObjectInputStream dis, ObjectOutputStream dos) throws IOException {

        String sn = song_name.toLowerCase();
        System.out.println(sn);
        char song = sn.charAt(0);

        if (song >= this.from && song <= this.to) {
            List<MusicFile> list = new ArrayList<MusicFile>();
            MusicFile music = new MusicFile();
            Mp3Parse parse = new Mp3Parse();

            for (int i = 0; i < filenames.size(); i++) {
                if (filenames.get(i).equals(song_name)) {
                    music = songs.get(i);
                    list = parse.chunks(music.getMusicFileExtract(), music);
                }
            }
            music.printTrack();
            System.out.println("chunks : "+ list.size());

            byte[] be = new byte[0];
            music.setMusicFileExtract(be);
            music.printTrack();

            dos.writeObject(music);
            dos.writeObject(list.size());


            /*for (int i = 0; i < list.size(); i++) {
                dos.writeObject(list.get(i));
            }*/
        }
    }

    public void beginHash() throws IOException {

        //iportName = MD5(getIP() + Integer.toString(port));

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
}
