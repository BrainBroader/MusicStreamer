import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class BClient2 extends Thread {

    private String IP;
    private int PORT;
    private String song;
    private ActionsForConsumer actions;
    private ArrayList<MusicFile> musfile;
    private int chunkSize;

    public BClient2(String IP, int PORT, String song, ActionsForConsumer actions, ArrayList<MusicFile> musfile, int chunkSize) {
        this.IP = IP;
        this.PORT = PORT;
        this.song = song;
        this.actions = actions;
        this.musfile = musfile;
        this.chunkSize = chunkSize;
    }

    @Override
    public void run() {
        Socket s = null;
        InetAddress ip = null;
        ObjectOutputStream dos = null;
        ObjectInputStream dis = null;

        try {
            ip = InetAddress.getByName(IP);
            s = new Socket(ip, PORT);

            dos = new ObjectOutputStream(s.getOutputStream());
            dis = new ObjectInputStream(s.getInputStream());

            dos.writeObject("Song");
            dos.writeObject(this.song);

            MusicFile msong = new MusicFile();
            msong = (MusicFile) dis.readObject();
            msong.printTrack();


            musfile.add(msong);


            int chunks_size = (int) dis.readObject();

            if (chunks_size != 0) {
                chunkSize = chunks_size;
            }


            /*for (int i = 0; i < chunks_size; i++) {
                byte[] chunk = (byte[]) dis.readObject();
                this.actions.getB().addQueue(chunk);
            }*/


        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                dis.close();
                dos.close();
                s.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
