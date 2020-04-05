import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class ConHandler extends Thread {

    private String IP;
    private int PORT;
    private Consumer consumer;
    private static ArrayList<String> brokers_ip;
    private static ArrayList<Integer> brokers_ports;

    public ConHandler(String IP, int PORT, Consumer consumer, ArrayList<String> brokers_ip, ArrayList<Integer> brokers_ports) {

        this.IP = IP;
        this.PORT = PORT;
        this.consumer = consumer;
        this.brokers_ip = brokers_ip;
        this.brokers_ports = brokers_ports;
    }

    @Override
    public void run()
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

                if (inputString.equals("exit")) {
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

                System.out.println("Choose song's number...");
                String input_song = scanner.nextLine();

                input_song = list.get(Integer.parseInt(input_song) - 1);
                System.out.println(input_song);

                dos.writeObject(input_song);

                MusicFile music_file = new MusicFile();
                music_file = (MusicFile) dis.readObject();

                int chunk_size = (int) dis.readObject();

                /*ArrayList<byte[]> c = new ArrayList<>();
                for (int i = 0; i < chunk_size; i++) {
                    byte[] chunk = (byte[]) dis.readObject();
                    c.add(chunk);
                }*/

                music_file.printTrack();
                System.out.println("chunks : "+chunk_size);


            }

            dis.close();
            dos.close();
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}

/*System.out.println("ela kayla");

                    for (int i = 0; i < brokers_ports.size(); i++) {

                        if (brokers_ports.get(i) == PORT) {
                            brokers_ports.remove(i);
                        }
                        if (brokers_ip.get(i).equals(IP)) {
                            brokers_ip.remove(i);
                        }
                    }

                    Random r = new Random();
                    int number = r.nextInt(brokers_ip.size());

                    s.close();
                    dis.close();
                    dos.close();

                    ip = InetAddress.getByName(brokers_ip.get(number));
                    s = new Socket(ip, (brokers_ports.get(number)));

                    dos = new ObjectOutputStream(s.getOutputStream());
                    dis = new ObjectInputStream(s.getInputStream());

                    dos.writeObject("reconnect");*/