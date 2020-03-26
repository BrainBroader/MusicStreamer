import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class PubHandler extends Thread {

    private String IP;
    private int PORT;
    //private MethodPubHadler mb;

    public PubHandler(String IP, int PORT) {
        this.IP = IP;
        this.PORT = PORT;
    }


    @Override
    public void run()
    {
        doyourJob();
    }



    public synchronized void doyourJob() {
        try
        {
            Scanner scn = new Scanner(System.in);


            // getting localhost ip
            InetAddress ip = InetAddress.getByName(IP);

            // establish the connection with server port 5056
            Socket s = new Socket(ip, PORT);

            // obtaining input and out streams
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            // the following loop performs the exchange of
            // information between client and client handler
            while (true)
            {
                System.out.println(dis.readUTF());
                String tosend = scn.nextLine();
                dos.writeUTF(tosend);

                // If client sends exit,close this connection
                // and then break from the while loop
                if(tosend.equals("Exit"))
                {
                    System.out.println("[SERVER] Closing this connection : " + s);
                    s.close();
                    System.out.println("[SERVER] Connection closed");
                    break;
                }

                // printing date or time as requested by client
                String received = dis.readUTF();
                System.out.println(received);
            }

            // closing resources
            //scn.close();
            dis.close();
            dos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


}
