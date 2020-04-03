import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class BClient extends Thread {

    private String IP;
    private int PORT;
    private String artist;
    private ActionsForConsumer actions;
    private static ArrayList<String> a_songs;

    public BClient(String IP, int PORT, String artist, ActionsForConsumer actions, ArrayList<String> a_songs) {
        this.IP = IP;
        this.PORT = PORT;
        this.artist = artist;
        this.actions = actions;
        this.a_songs = a_songs;
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

            dos.writeObject(this.artist);

            ArrayList<String> art_list = new ArrayList<>();
            art_list = (ArrayList<String>) dis.readObject();

            for (int i = 0; i < art_list.size(); i++) {
                a_songs.add(art_list.get(i));
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }





}
