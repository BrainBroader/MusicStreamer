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
                System.out.println("ELA ELA ELA ");
            }

            if (exit.equals("no") || r.equals("reconnect")) {

                String artist = (String) dis.readObject();
                System.out.println("Artist : " +artist);

                Socket socket = null;
                InetAddress ipp = null;
                ObjectOutputStream out = null;
                ObjectInputStream in = null;

                try {
                    ipp = InetAddress.getByName("192.168.1.12");
                    socket = new Socket(ipp, 9090);

                    out = new ObjectOutputStream(socket.getOutputStream());
                    in = new ObjectInputStream(socket.getInputStream());

                    out.writeObject(artist);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }




        /*if (r.equals("reconnect")) {
            stream_reconnect();
        } else {
            stream();
        }*/

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
