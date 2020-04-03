import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class ConHandler extends Thread {

    private String IP;
    private int PORT;
    private Consumer consumer;

    public ConHandler(String IP, int PORT, Consumer consumer) {

        this.IP = IP;
        this.PORT = PORT;
        this.consumer = consumer;
    }

    @Override
    public synchronized void run()
    {
        InetAddress ip = null;
        Socket s = null;
        ObjectOutputStream dos = null;
        ObjectInputStream dis = null;
        try
        {
            ip = InetAddress.getByName(IP);

            s = new Socket(ip, PORT);

            dos = new ObjectOutputStream(s.getOutputStream());
            dis = new ObjectInputStream(s.getInputStream());


            String p = "Consumer";
            dos.writeObject(p);


            HashMap<String, String> bl = new HashMap<>();
            bl = (HashMap<String, String>) dis.readObject();
            consumer.setBroker_list(bl);

            System.out.println(bl);

            ArrayList<String> artists = new ArrayList<>();
            artists = (ArrayList<String>) dis.readObject();

            while(true) {
                for (int i = 0; i < artists.size(); i++) {
                    System.out.println(i + 1 + ". " + artists.get(i));
                }

                System.out.println("Choose an number...");
                Scanner scanner = new Scanner(System.in);
                String inputString = scanner.nextLine();

                if(inputString.equals("exit")){
                    break;
                }

                inputString = artists.get(Integer.parseInt(inputString) - 1);

                String iportname = consumer.getBroker_list().get(inputString);
                //System.out.println(iportname);

                String[] splited = iportname.split("\\s+");

                if (!(splited[0].equals(IP) && (Integer.parseInt(splited[1]) == PORT))) {
                    dos.writeObject("yes");
                    s.close();
                    dis.close();
                    dos.close();

                    ip = InetAddress.getByName(splited[0]);
                    s = new Socket(ip, Integer.parseInt(splited[1]));

                    dos = new ObjectOutputStream(s.getOutputStream());
                    dis = new ObjectInputStream(s.getInputStream());

                    dos.writeObject("reconnect");

                } else {
                    String exit = "no";
                    dos.writeObject(exit);
                }
                dos.writeObject(inputString);

                ArrayList<String> list = (ArrayList<String>) dis.readObject();

                for (int i = 0; i < list.size(); i++) {
                    System.out.println((i + 1) + ". " + list.get(i));
                }
                System.out.println(" ");
            }

            dis.close();
            dos.close();
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
