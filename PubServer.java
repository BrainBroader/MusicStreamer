import MusicFile.MusicFile;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class PubServer extends Thread {

    final ObjectInputStream dis;
    final ObjectOutputStream dos;
    final Socket s;
    private int PORT;
    private Publisher p;


    public PubServer(Socket s, ObjectInputStream dis, ObjectOutputStream dos, int PORT, Publisher p)
    {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
        this.PORT = PORT;
        this.p = p;
    }


    @Override
    public void run() {

        try {

            String artist = (String) dis.readObject();

            ArrayList<String> art_fnames = new ArrayList<>();

            for (int i = 0; i < p.getSongs().size(); i++) {
                if (artist.equals(p.getSongs().get(i).getArtistName())) {
                    art_fnames.add(p.getFilenames().get(i));
                }
            }

            dos.writeObject(art_fnames);


            String song_name = (String) dis.readObject();
            int j;

            for (j = 0; j < p.getFilenames().size(); j++) {
                if (p.getFilenames().get(j).equals(song_name)) {
                    break;
                }
            }

            MusicFile music = new MusicFile();
            Mp3Parse parse = new Mp3Parse();

            List<MusicFile> list = parse.chunks(p.getSongs().get(j).getMusicFileExtract(), p.getSongs().get(j));

            dos.writeObject(list.size());
            push(list, dos);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public synchronized void push(List<MusicFile> list, ObjectOutputStream out) throws IOException, ClassNotFoundException {
        for (int i = 0; i < list.size(); i++) {
            out.writeObject(list.get(i));
        }
    }
}
