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
            System.out.println("4. received "+artist);

            ArrayList<String> art_fnames = new ArrayList<>();

            for (int i = 0; i < p.getSongs().size(); i++) {
                if (artist.equals(p.getSongs().get(i).getArtistName())) {
                    art_fnames.add(p.getFilenames().get(i));
                }
            }

            dos.writeObject(art_fnames);
            System.out.println("5. sending "+art_fnames);


            ////////////////////////////////////////////
            String song_name = (String) dis.readObject();
            System.out.println("12. received "+song_name);

            int j;

            for (j = 0; j < p.getFilenames().size(); j++) {
                if (p.getFilenames().get(j).equals(song_name)) {
                    break;
                }
            }

            MusicFile music = new MusicFile();
            Mp3Parse parse = new Mp3Parse();

            music.setTrackName(p.getSongs().get(j).getTrackName());
            music.setArtistName(p.getSongs().get(j).getArtistName());
            music.setAlbumInfo(p.getSongs().get(j).getAlbumInfo());
            music.setGenre(p.getSongs().get(j).getGenre());

            List<MusicFile> list = parse.chunks(p.getSongs().get(j).getMusicFileExtract(), p.getSongs().get(j));

            dos.writeObject(music);
            dos.writeObject(list.size());
            System.out.println("13. sending tags: "+music);
            System.out.println("14. sendings chunks: "+list.size());


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
