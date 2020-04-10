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
    private String job;

    public PubHandler(String IP, int PORT, Publisher pub, String job) {
        this.IP = IP;
        this.PORT = PORT;
        this.pub = pub;
        this.job = job;
    }


    @Override
    public void run()
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



            if (job.equals("connect")) {
                String p = "Publisher";
                dos.writeObject(p);

                dos.writeObject(pub.getArtistsList());
                dos.writeObject(pub.getBrokers_ip());
                dos.writeObject(pub.getBrokers_ports());
                dos.writeObject(pub.getBrokers_list());
                dos.writeObject(pub.getPub_server());
            } else if (job.equals("reconnect")) {
                String p = "PubVol2";
                dos.writeObject(p);
                dos.writeObject(pub.getBrokers_list());
            }

            /*dos.writeObject(pub);

            Broker b = (Broker) dis.readObject();
            if (!pub.getBrokers().contains(b)) {
                pub.addBrokers(b);
            }*/


            s.close();


        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
