import java.io.*; 
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

class ActionsForPub extends Thread
{
    final ObjectInputStream dis;
    final ObjectOutputStream dos;
    final Socket s;
    private int PORT;
    private Broker b;
      
  
    // Constructor 
    public ActionsForPub(Socket s, ObjectInputStream dis, ObjectOutputStream dos, int PORT, Broker b)
    { 
        this.s = s;
        this.dis = dis;
        this.dos = dos;
        this.PORT = PORT;
        this.b = b;
    } 
  
    @Override
    public void run() {

        push_response();
    }

    public synchronized void push_response() {

        String received;
        String toreturn;



        try {

            ArrayList<String> artists = new ArrayList<>();
            artists = (ArrayList<String>) dis.readObject();
            b.setArtists(artists);

            for (int i = 0; i < b.getArtists().size(); i++) {
                System.out.println(b.getArtists().get(i));
            }

            ArrayList<String> ips = new ArrayList<>();
            ips = (ArrayList<String>) dis.readObject();
            b.setBrokers_ip(ips);

            ArrayList<Integer> ports = new ArrayList<>();
            ports = (ArrayList<Integer>) dis.readObject();
            b.setBrokers_ports(ports);

            for (int i = 0; i < b.getBrokers_ip().size(); i++) {
                System.out.println(b.getBrokers_ip().get(i) +" "+ b.getBrokers_ports().get(i));
            }

            b.beginHash();

            dos.writeObject(b.getBrokers_list());




            //b.getIpp();


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}