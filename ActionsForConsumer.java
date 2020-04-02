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

            while(true) {
               String exit = (String) dis.readObject();

               if (exit.equals("no")) {
                   stream_reconnect();
               }
               else if(exit.equals("yes")){
                   break;
               }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public synchronized void stream_reconnect() {

        try {

            String artist = (String) dis.readObject();
            System.out.println("Artist : " +artist);

            while (b.getFlag()) {
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
            b.arrive();



        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



}
