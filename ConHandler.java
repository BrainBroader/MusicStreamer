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

        while (true) {

            try {
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

                ArrayList<String> keyboard = new ArrayList<>();
                for (int i = 1; i <= artists.size(); i++) {
                    keyboard.add(String.valueOf(i));
                }
                keyboard.add("exit");


                for (int i = 0; i < artists.size(); i++) {
                    System.out.println(i + 1 + ". " + artists.get(i));
                }

                System.out.println("Choose an number...");
                Scanner scanner = new Scanner(System.in);
                String inputString = scanner.nextLine();


                while (!keyboard.contains(inputString)) {
                    System.out.println("This artist doesn't exist.");
                    System.out.println("Please try again");
                    inputString = scanner.nextLine();
                }

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
                System.out.println("1. sending " + inputString);

                ArrayList<String> list = (ArrayList<String>) dis.readObject();
                System.out.println("8. arrived");
                ArrayList<String> keyboard2 = new ArrayList<>();
                keyboard2.add("exit");

                for (int i = 0; i < list.size(); i++) {
                    System.out.println((i + 1) + ". " + list.get(i));
                    keyboard2.add(String.valueOf(i + 1));
                }

                System.out.println("Choose song's number...");
                String input_song = scanner.nextLine();

                while (!keyboard2.contains(input_song)) {
                    System.out.println("This song doesn't exist.");
                    System.out.println("Please try again");
                    input_song = scanner.nextLine();
                }

                if (input_song.equals("exit")) {
                    break;
                }

                input_song = list.get(Integer.parseInt(input_song) - 1);
                System.out.println(input_song);
                System.out.println("----------------------------------------");

                dos.writeObject(input_song);
                System.out.println("9. sending " + input_song);

                MusicFile music_file = new MusicFile();
                music_file = (MusicFile) dis.readObject();

                int chunk_size = (int) dis.readObject();
                System.out.println("19. arrived. tags ");
                System.out.println("20. arrived chunks ");

            /*ArrayList<byte[]> c = new ArrayList<>();
            for (int i = 0; i < chunk_size; i++) {
                byte[] chunk = (byte[]) dis.readObject();
                c.add(chunk);
            }*/

                music_file.printTrack();
                System.out.println("chunks : " + chunk_size);
                System.out.println();


                dis.close();
                dos.close();
                s.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
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