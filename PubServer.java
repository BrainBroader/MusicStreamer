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


    //private static ArrayList<String> art_songs;


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


            ////////////////////////////////////////////
            String song_name = (String) dis.readObject();

            int j;

            for (j = 0; j < p.getFilenames().size(); j++) {
                if (p.getFilenames().get(j).equals(song_name)) {
                    break;
                }
            }


            List<MusicFile> list = new ArrayList<>();
            MusicFile music = new MusicFile();
            Mp3Parse parse = new Mp3Parse();

            String sn = song_name.toLowerCase();
            //System.out.println(song_name);
            char song = sn.charAt(0);

            music = p.getSongs().get(j);
            list = parse.chunks(music.getMusicFileExtract(), music);


            //music.printTrack();
            System.out.println("chunks : " + list.size());

            byte[] be = new byte[0];
            music.setMusicFileExtract(be);
            //music.printTrack();

            dos.writeObject(music);
            dos.writeObject(list.size());


            /*for (int i = 0; i < list.size(); i++) {
                dos.writeObject(list.get(i));
            }*/

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }


}
