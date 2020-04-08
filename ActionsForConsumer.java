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

    //private static ArrayList<String> art_songs;


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
                dos.writeObject(b.getArtists());
            }

            String exit = "";

            if (r.equals("firstTime")) {
                exit = (String) dis.readObject();
            }

            if (exit.equals("no") || r.equals("reconnect")) {

                String artist = (String) dis.readObject();
                System.out.println("2. received "+artist);

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

                out.writeObject(artist);
                System.out.println("3. sending "+ artist);

                ArrayList<String> art_list = new ArrayList<>();
                art_list = (ArrayList<String>) in.readObject();
                System.out.println("6. received "+art_list);

                for (int i = 0; i < art_list.size(); i++) {
                    art_songs.add(art_list.get(i));
                }

                dos.writeObject(art_songs);
                System.out.println("7. sending "+art_songs);


                String song_name = (String) dis.readObject();
                System.out.println("10. received "+song_name );
                System.out.println(song_name);

                out.writeObject(song_name);
                System.out.println("11. sending "+song_name );

                //MusicFile mus = (MusicFile) in.readObject();
                int chunks_size = (int) in.readObject();
                //System.out.println("15. received tags: "+mus);
                System.out.println("14. sendings chunks: "+chunks_size);

                //mus.printTrack();
                //dos.writeObject(mus);
                dos.writeObject(chunks_size);
                //System.out.println("17. sending tags: "+mus);
                System.out.println("15. sendings chunks: "+chunks_size);

                pull(chunks_size, dos, in);


            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public synchronized void pull(int chunks_size, ObjectOutputStream dos, ObjectInputStream in) throws IOException, ClassNotFoundException {
        for (int i = 0; i < chunks_size; i++) {
            MusicFile chunk = (MusicFile) in.readObject();
            //chunk.printTrackInfo();
            dos.writeObject(chunk);
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

    public void lock()throws InterruptedException
    {
        synchronized(this)
        {
            wait();
        }
    }

    public void unlock()throws InterruptedException
    {
        synchronized(this)
        {
            notify();
        }
    }


    /*public synchronized void stream() {

        String received;
        String toreturn;

        try {





        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }*/

    /*public synchronized void stream_reconnect() {

        Socket s = null;
        InetAddress ip = null;
        ObjectOutputStream dos = null;
        ObjectInputStream dis = null;

        try {

            String artist = (String) dis.readObject();
            System.out.println("Artist : " +artist);



            while (!b.getFlag2()) {
                try {
                    b.send();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            ArrayList<String> list = new ArrayList<>();

            int s = b.getPubTocon().size();
            for (int i = 0; i < s; i++) {
                String removed = b.getPubTocon().remove();
                list.add(removed);
            }

            dos.writeObject(list);
            b.setFlag2(false);
            b.arrive();



        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }*/



}
