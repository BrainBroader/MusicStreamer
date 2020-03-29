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
    private static ArrayList<Integer> brokers_ports = new ArrayList<Integer>();
    private static ArrayList<String> brokers_ip = new ArrayList<String>();
    private static ArrayList<String> artists = new ArrayList<String>();
    private static ArrayList<List<MusicFile>> songs = new ArrayList<>();
    private static String IP;
    //private static int Brokers;

    public static void main(String[] args) throws Exception {

        ArrayList<MusicFile> array = new ArrayList<MusicFile>();
        String filepath = "C:\\Users\\MrAG99\\IdeaProjects\\MusicStreamer\\dataset2";
        Path dir = FileSystems.getDefault().getPath(filepath);
        DirectoryStream<Path> stream = Files.newDirectoryStream( dir );
        for (Path path : stream) {
            //System.out.println(path.getFileName());
            String p = filepath + "/" +(path.getFileName()).toString();
            Mp3Parse parse = new Mp3Parse();
            MusicFile m = new MusicFile();
            m = parse.mp3extraction(p);
            array.add(m);
            addArtist(m.getArtistName());

            /*List<MusicFile> list = new ArrayList<MusicFile>();
            list = parse.chunks(m.getMusicFileExtract(), m);
            songs.add(list);*/
        }
        stream.close();

        System.out.println("Tracks : "+array.size());

        /*for (int i = 0; i < artists.size(); i++) {
            BigInteger hash = MD5(artists.get(i));
            System.out.println(hash);
        }*/

        Publisher pub = new Publisher();
        pub.connect();

    }

    public void connect() {
        loadPorts("brokers1.txt");

        for (int i = 0; i < brokers_ip.size(); i++) {
            PubHandler handler = new PubHandler(brokers_ip.get(i), brokers_ports.get(i), this);
            handler.start();
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
                artists.add(artist);
                System.out.println(artist);
            }
        }
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




    HashMap<Integer,String> ArtistComp = new HashMap<>();

    //Hashing the artist name and return a Broker instance
    public void hashTopic(String name) {
        Broker b = new Broker();
        ArtistComp.put(b.hashTopic(name),name);
    }




} 