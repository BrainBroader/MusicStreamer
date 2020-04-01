import java.io.*;
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

    public ActionsForConsumer(Socket s, ObjectInputStream dis, ObjectOutputStream dos, int PORT, Broker b)
    {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
        this.PORT = PORT;
        this.b = b;
        this.r = "no";
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
        if (r.equals("reconnect")) {
            stream_reconnect();
        } else {
            stream();
        }

    }


    public synchronized void stream() {

        String received;
        String toreturn;

        try {

            dos.writeObject(b.getBrokers_list());
            dos.writeObject(b.getArtists());

            String exit = (String) dis.readObject();

            if(exit.equals("no")) {
                stream_reconnect();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public synchronized void stream_reconnect() {

        String received;
        String toreturn;

        try {

            String artist = (String) dis.readObject();
            //System.out.println("Artist : " +artist);
            (b.getConTopub()).add(artist);

            Queue<String> n = b.getPubTocon();
            while (n.size() == 0) {
                n = b.getPubTocon();
            }

            ArrayList<String> list = new ArrayList<>();

            int s = b.getPubTocon().size();
            //System.out.println(s);
            for (int i = 0; i < s; i++) {
                String removed = b.getPubTocon().remove();
                list.add(removed);
            }

            dos.writeObject(list);



        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }



}
