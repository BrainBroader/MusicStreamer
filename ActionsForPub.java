import java.io.*; 
import java.net.*;
import java.util.*;

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

        try {

            ArrayList<String> artists = new ArrayList<>();
            artists = (ArrayList<String>) dis.readObject();
            b.saveArtists(artists);

            ArrayList<String> ips = new ArrayList<>();
            ips = (ArrayList<String>) dis.readObject();
            b.setBrokers_ip(ips);

            ArrayList<Integer> ports = new ArrayList<>();
            ports = (ArrayList<Integer>) dis.readObject();
            b.setBrokers_ports(ports);

            HashMap<String, String> bl = new HashMap<>();
            bl = (HashMap<String, String>) dis.readObject();


            for (Map.Entry me : bl.entrySet()) {
                b.putBroker_list((String)me.getKey(), (String)me.getValue());
                //System.out.println("Key: "+me.getKey() + " & Value: " + me.getValue());
            }

            Publisher p = (Publisher) dis.readObject();
            b.addPublisher(p);

            dos.writeObject(b);

            System.out.println("Closing...");
            System.out.println("Connection closed");



        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                dis.close();
                dos.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    /*public synchronized void push_response() {

        String received;
        String toreturn;



        try {

            /*ArrayList<String> artists = new ArrayList<>();
            artists = (ArrayList<String>) dis.readObject();
            b.setArtists(artists);*/

            /*for (int i = 0; i < b.getArtists().size(); i++) {
                System.out.println(b.getArtists().get(i));
            }*/

            /*ArrayList<String> ips = new ArrayList<>();
            ips = (ArrayList<String>) dis.readObject();
            b.setBrokers_ip(ips);

            ArrayList<Integer> ports = new ArrayList<>();
            ports = (ArrayList<Integer>) dis.readObject();
            b.setBrokers_ports(ports);*/

            /*for (int i = 0; i < b.getBrokers_ip().size(); i++) {
                System.out.println(b.getBrokers_ip().get(i) +" "+ b.getBrokers_ports().get(i));
            }

            b.beginHash();

            dos.writeObject(b.getBrokers_list());
            while(true) {

                while (!b.getFlag()) {
                    try {
                        b.send();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                dos.writeObject(b.getConTopub().remove());
                b.setFlag(false);
                b.arrive();

                ArrayList<String> list = new ArrayList<>();
                list = (ArrayList<String>) dis.readObject();

                while (b.getFlag2()) {
                    try {
                        b.send();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                for (int i = 0; i < list.size(); i++) {
                    (b.getPubTocon()).add(list.get(i));
                }

                b.setFlag2(true);
                b.arrive();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }*/
}