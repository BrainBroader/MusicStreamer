import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

public class PubHandler extends Thread {

    private String IP;
    private int PORT;
    private Publisher pub;

    public PubHandler(String IP, int PORT, Publisher pub) {
        this.IP = IP;
        this.PORT = PORT;
        this.pub = pub;
    }


    @Override
    public synchronized void run()
    {
        Socket s = null;
        InetAddress ip = null;
        ObjectOutputStream dos = null;
        ObjectInputStream dis = null;

        try {
            ip = InetAddress.getByName(IP);
            s = new Socket(ip, PORT);

            dos = new ObjectOutputStream(s.getOutputStream());
            dis = new ObjectInputStream(s.getInputStream());

            String p = "Publisher";
            dos.writeObject(p);

            dos.writeObject(pub.getArtistsList());
            dos.writeObject(pub.getBrokers_ip());
            dos.writeObject(pub.getBrokers_ports());

            HashMap<BigInteger, String> hash_brokers = new HashMap<>();
            hash_brokers = (HashMap<BigInteger, String>) dis.readObject();
            pub.setHash_brokers(hash_brokers);

            HashMap<String, String> bl = new HashMap<>();
            bl = (HashMap<String, String>) dis.readObject();
            pub.setBroker_list(bl);

            Broker b = (Broker) dis.readObject();
            if (!pub.getBrokers().contains(b)) {
                pub.addBrokers(b);
            }


            s.close();


        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*public synchronized void push(ObjectInputStream dis, ObjectOutputStream dos) {
        try
        {





            String p = "Publisher";
            dos.writeObject(p);

            dos.writeObject(pub.getArtitsList()); //LATHOS STO ARTISTS
            dos.writeObject(pub.getBrokers_ip());
            dos.writeObject(pub.getBrokers_ports());

            HashMap<String, String> bl = new HashMap<>();
            bl = (HashMap<String, String>) dis.readObject();
            pub.setBroker_list(bl);

            //System.out.println(bl);

            while (true) {

                String art = (String) dis.readObject();

                if (art.equals("exit")) {
                    break;
                }

                ArrayList<String> art_fnames = new ArrayList<>();

                for (int i = 0; i < pub.getSongs().size(); i++) {
                    if (art.equals(pub.getSongs().get(i).getArtistName())) {
                        art_fnames.add(pub.getFilenames().get(i));
                    }
                }

                /*for (int i = 0; i < art_fnames.size(); i++) {
                    System.out.println(art_fnames.get(i));
                }

                dos.writeObject(art_fnames);
            }



            // closing resources
            dis.close();
            dos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }*/




}
