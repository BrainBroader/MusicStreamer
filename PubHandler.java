import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
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
    public void run()
    {
        push();
    }

    public synchronized void push() {
        try
        {

            // getting localhost ip
            InetAddress ip = InetAddress.getByName(IP);

            // establish the connection with server port 5056
            Socket s = new Socket(ip, PORT);

            // obtaining input and out streams
            ObjectOutputStream dos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream dis = new ObjectInputStream(s.getInputStream());



            String p = "Publisher";
            dos.writeObject(p);

            dos.writeObject(pub.getArtitsList());
            dos.writeObject(pub.getBrokers_ip());
            dos.writeObject(pub.getBrokers_ports());

            HashMap<String, String> bl = new HashMap<>();
            bl = (HashMap<String, String>) dis.readObject();
            pub.setBroker_list(bl);

            System.out.println(bl);

            while (true) {
                if (p.equals("exit")) {
                    break;
                }
            }




            // closing resources
            dis.close();
            dos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }




}
