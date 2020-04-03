import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
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
    public synchronized void run()
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

                ArrayList<String> ips = new ArrayList<String>();
                ArrayList<Integer> ports = new ArrayList<Integer>();
                ArrayList<BClient> bc_list = new ArrayList<>();
                ArrayList<String> art_songs = new ArrayList<>();
                loadPorts("Publishers2.txt", ips, ports);

                for (int i = 0; i < ips.size(); i++) {
                    BClient bc = new BClient(ips.get(i), ports.get(i), artist, this, art_songs);
                    bc_list.add(bc);
                    bc.start();
                }

                for (int i = 0; i < bc_list.size(); i++) {
                    try {
                        bc_list.get(i).join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                dos.writeObject(art_songs);


            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
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

    public synchronized void stream_reconnect() {

        Socket s = null;
        InetAddress ip = null;
        ObjectOutputStream dos = null;
        ObjectInputStream dis = null;

        try {

            String artist = (String) dis.readObject();
            System.out.println("Artist : " +artist);



            /*while (b.getFlag()) {
                try {
                    b.send();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //System.out.println("b.getFlag()");
            }
            (b.getConTopub()).add(artist);
            b.setFlag(true);
            b.arrive();

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
            b.arrive();*/



        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } /*catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }



}
