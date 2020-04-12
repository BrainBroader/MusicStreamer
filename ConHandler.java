import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

public class ConHandler extends Thread {

    private String IP;
    private int PORT;
    private Consumer consumer;
    private static ArrayList<String> brokers_ip;
    private static ArrayList<Integer> brokers_ports;
    private static ArrayList<String> arts = new ArrayList<>(); //

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
                    keyboard.add(artists.get(i-1).toLowerCase());
                    arts.add(artists.get(i-1));
                }
                keyboard.add("exit");


                for (int i = 0; i < artists.size(); i++) {
                    System.out.println(i + 1 + ". " + artists.get(i));
                }

                System.out.println("Choose an number...");
                Scanner scanner = new Scanner(System.in);
                String inputString = scanner.nextLine();
                inputString = inputString.toLowerCase();


                while (!keyboard.contains(inputString)) {
                    System.out.println("This artist doesn't exist.");
                    System.out.println("Please try again");
                    inputString = scanner.nextLine();
                    inputString = inputString.toLowerCase();
                }

                if (inputString.equals("exit")) {
                    break;
                }

                char ch = inputString.charAt(0);

                if (Character.isDigit(ch)) {
                    inputString = artists.get(Integer.parseInt(inputString) - 1);
                } else {
                    for (int i = 0; i < artists.size() ; i++) {
                        if ((artists.get(i).toLowerCase()).equals(inputString.toLowerCase())) {
                            inputString = artists.get(i);
                            break;
                        }
                    }
                }

                String iportname = consumer.getBroker_list().get(inputString);

                String[] splited = iportname.split("\\s+");


                if (!(splited[0].equals(IP) && (Integer.parseInt(splited[1]) == PORT))) {
                    dos.writeObject("yes");
                    s.close();
                    dis.close();
                    dos.close();

                    this.IP = splited[0];
                    this.PORT = Integer.parseInt(splited[1]);

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
                ArrayList<String> keyboard2 = new ArrayList<>();
                keyboard2.add("exit");

                for (int i = 0; i < list.size(); i++) {
                    System.out.println((i + 1) + ". " + list.get(i).substring(0, list.get(i).length() - 4));
                    keyboard2.add(String.valueOf(i + 1));
                    keyboard2.add(list.get(i).substring(0, list.get(i).length() - 4).toLowerCase());
                }

                System.out.println("Choose song's number...");
                String input_song = scanner.nextLine();
                input_song = input_song.toLowerCase();

                while (!keyboard2.contains(input_song)) {
                    System.out.println("This song doesn't exist.");
                    System.out.println("Please try again");
                    input_song = scanner.nextLine();
                    input_song = input_song.toLowerCase();
                }

                if (input_song.equals("exit")) {
                    break;
                }

                char ch2 = input_song.charAt(0);

                if (Character.isDigit(ch2)) {
                    input_song = list.get(Integer.parseInt(input_song) - 1);
                } else {
                    input_song = input_song + ".mp3";
                    for (int i = 0; i < list.size() ; i++) {
                        if ((list.get(i).toLowerCase()).equals(input_song.toLowerCase())) {
                            input_song = list.get(i);
                            break;
                        }
                    }
                }


                dos.writeObject(input_song);

                int chunk_size = (int) dis.readObject();

                List<MusicFile> array = new ArrayList<>();

                for (int i = 0; i < chunk_size; i++) {
                    MusicFile chunk = (MusicFile) dis.readObject();
                    array.add(chunk);
                }

                Mp3Parse parse = new Mp3Parse();

                System.out.println("Do you want to stream or to download it?");
                String move = scanner.nextLine();
                move = move.toLowerCase();

                while (!(move.equals("stream") || move.equals("download"))) {
                    System.out.println("Please try again");
                    move = scanner.nextLine();
                    move = move.toLowerCase();
                }

                if (move.equals("stream")) {
                    System.out.println("Playing "+ input_song + "...");
                    String name = input_song.substring(0, input_song.length() - 4);

                    for (int i = 0; i < array.size(); i++) {
                        String path = name + "-chunk" + (i+1) + ".mp3";
                        parse.createMP3(array.get(i), System.getProperty("user.dir") + "\\created_songs\\" + path);
                    }
                } else {
                    MusicFile merged = parse.reproduce(array);
                    parse.createMP3(merged, System.getProperty("user.dir") + "\\created_songs\\" + input_song);
                    merged.printTrack();
                    System.out.println(input_song + " saved.");
                }

                System.out.println();

                this.arts = new ArrayList<>();

                dis.close();
                dos.close();
                s.close();
            } catch (Exception e) {

                System.out.println("Server is down..Connecting to another server...");

                for (int i = 0; i < this.brokers_ports.size(); i++) {
                    if ((this.brokers_ports.get(i) == this.PORT) && (this.brokers_ip.get(i).equals(this.IP))) {
                        this.brokers_ports.remove(i);
                        this.brokers_ip.remove(i);
                        break;
                    }
                }
                if (this.brokers_ports.size() == 0) {
                    break;
                }
                Random r = new Random();
                int number = r.nextInt(this.brokers_ip.size());
                this.IP = this.brokers_ip.get(number);
                this.PORT = this.brokers_ports.get(number);

                Publisher p = new Publisher();
                p.setArtists(this.arts);
                p.setBrokers_ip(this.brokers_ip);
                p.setBrokers_ports(this.brokers_ports);
                p.setIpp(new ArrayList<>());
                p.setHash_ip(new HashMap<>());
                p.setH_artists(new ArrayList<>());
                p.setHash_art(new HashMap<>());

                this.arts = new ArrayList<>();

                try {
                    p.beginHash();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                p = new Publisher();
                p.connect("reconnect");
            }
        }
    }
}
