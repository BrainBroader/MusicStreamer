import MusicFile.MusicFile;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;

public class ActionsForConsumer extends Thread {

    final ObjectInputStream dis;
    final ObjectOutputStream dos;
    final Socket s;
    private int PORT;
    private Broker b;
    private static String r;


    public ActionsForConsumer(Socket s, ObjectInputStream dis, ObjectOutputStream dos, int PORT, Broker b)
    {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
        this.PORT = PORT;
        this.b = b;
        this.r = "firstTime";
    }

    public ActionsForConsumer(Socket s, ObjectInputStream dis, ObjectOutputStream dos, int PORT, Broker b, String r)
    {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
        this.PORT = PORT;
        this.b = b;
        this.r = r;
    }


    @Override
    public void run()
    {
        try {
            if (r.equals("firstTime")) {
                dos.writeObject(b.getBrokers_list());
                dos.flush();
                dos.writeObject(b.getArtists());
                dos.flush();
            }

            String exit = "";

            if (r.equals("firstTime")) {
                exit = (String) dis.readObject();
            }

            if (exit.equals("no") || r.equals("reconnect") || r.equals("part2")) {

                String artist = (String) dis.readObject();

                ArrayList<String> ips = new ArrayList<>();
                ArrayList<Integer> ports = new ArrayList<>();
                ArrayList<String> art_songs = new ArrayList<>();
                loadPorts("Publishers2.txt", ips, ports);
                String iport = "";

                for (Map.Entry m : b.getPub_servers().entrySet()) {
                    ArrayList<String> server = (ArrayList<String>)m.getKey();
                    for (int i = 0; i < server.size(); i++) {
                        if (artist.equals(server.get(i))) {
                            iport = (String)m.getValue();
                        }
                    }
                }

                String[] splited = iport.split("\\s+");

                Socket socket = null;
                InetAddress ip = null;
                ObjectOutputStream out = null;
                ObjectInputStream in = null;


                ip = InetAddress.getByName(splited[0]);
                socket = new Socket(ip, Integer.parseInt(splited[1]));

                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                if (exit.equals("no") || r.equals("reconnect")) {
                    out.writeObject("part1");
                    out.flush();
                    out.writeObject(artist);
                    out.flush();

                    ArrayList<String> art_list = new ArrayList<>();
                    art_list = (ArrayList<String>) in.readObject();

                    for (int i = 0; i < art_list.size(); i++) {
                        art_songs.add(art_list.get(i));
                    }

                    dos.writeObject(art_songs);
                    dos.flush();

                } else if (r.equals("part2")) {

                    String song_name = (String) dis.readObject();
                    out.writeObject("part2");
                    out.flush();

                    out.writeObject(song_name);
                    out.flush();

                    int chunks_size = (int) in.readObject();

                    dos.writeObject(chunks_size);
                    dos.flush();
                    pull(chunks_size, dos, in);
                }


            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public synchronized void pull(int chunks_size, ObjectOutputStream dos, ObjectInputStream in) throws IOException, ClassNotFoundException {
        for (int i = 0; i < chunks_size; i++) {
            MusicFile chunk = (MusicFile) in.readObject();
            dos.writeObject(chunk);
            dos.flush();
        }
    }

    public Broker getB() {
        return this.b;
    }

    public static void loadPorts(String data, ArrayList<String> brokers_ip, ArrayList<Integer> brokers_ports) {
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
