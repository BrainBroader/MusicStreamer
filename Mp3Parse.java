import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;



public class Mp3Parse {

    public Mp3Parse() {

    }

    public static MusicFile mp3extraction(String path) throws Exception, InvalidDataException {

        String trackName = null;
        String artistName = null;
        String albumInfo = null;
        String genre = null;
        byte[] musicFileExtract;

        musicFileExtract = Files.readAllBytes(Paths.get(path));

        Mp3File mp3file = new Mp3File(path);
        if(mp3file.hasId3v1Tag()){
            ID3v1 tag1 = mp3file.getId3v1Tag();
            genre = tag1.getGenreDescription();
            artistName = tag1.getArtist();
            albumInfo = tag1.getAlbum();
            trackName = tag1.getTitle();
        }
        else if(mp3file.hasId3v2Tag()){
            ID3v2 tag2 = mp3file.getId3v2Tag();
            genre = tag2.getGenreDescription();
            artistName = tag2.getArtist();
            albumInfo = tag2.getAlbum();
            trackName = tag2.getTitle();
        }

        //creating a musicfile object
        MusicFile song = new MusicFile(trackName,artistName,albumInfo,genre,musicFileExtract);

        return song;
    }

    public static List<MusicFile> chunks(byte[] array, MusicFile music) throws IOException {
        List<MusicFile> ret = new ArrayList<MusicFile>();

        int bytes = array.length;
        int loops = 0;
        if (bytes % 64000 == 0) {
            loops = bytes / 64000;
        } else {
            loops = (bytes / 64000) + 1;
        }


        for (int i = 0; i < loops; i++) {

            if (i == loops - 1) {

                byte[] a = new byte[array.length - i * 64000];
                for (int j = 0; j < a.length; j++) {
                    a[j] = array[(i * 64000) + j];
                }
                MusicFile m = new MusicFile(music.getTrackName(),music.getArtistName(),music.getAlbumInfo(),music.getGenre(),a);
                ret.add(m);

            } else {
                byte[] a = new byte[64000];
                for (int j = 0; j < 64000; j++) {
                    a[j] = array[(i * 64000) + j];
                }
                MusicFile m = new MusicFile(music.getTrackName(),music.getArtistName(),music.getAlbumInfo(),music.getGenre(),a);
                ret.add(m);
            }

        }

        return ret;

    }

    public static void createMP3(MusicFile m, String path) throws IOException {

        File f = null;

        try	{
            f = new File(path);
        }
        catch (NullPointerException e) {
            System.out.println ("Can't create file");
        }

        FileOutputStream outputstream = new FileOutputStream(f);
        outputstream.write(m.getMusicFileExtract());

        outputstream.close();
        System.out.println("Creating Mp3... "+ path);
    }

    public static void main(String args[])
    {

        try {
           ArrayList<MusicFile> array = new ArrayList<MusicFile>();
            String filepath = "D:\\ΓΙΩΡΓΟΣ ΣΥΜΕΩΝΙΔΗΣ\\Documents\\6ο ΕΞΑΜΗΝΟ\\ΚΑΤΑΝΕΜΗΜΕΝΑ ΣΥΣΤΗΜΑΤΑ\\MusicStreamer-spotify\\dataset2";
            Path dir = FileSystems.getDefault().getPath(filepath);
            DirectoryStream<Path> stream = Files.newDirectoryStream( dir );
            for (Path path : stream) {
                //System.out.println(path.getFileName());
                String p = filepath + "/" +(path.getFileName()).toString();
                MusicFile m = new MusicFile();
                m = mp3extraction(p);
                m.printTrack();
                //List<MusicFile> list = new ArrayList<MusicFile>();
                //list = chunks(m.getMusicFileExtract(), m);
                //createMP3(m, path.getFileName().toString());
                array.add(m);
            }
            stream.close();

            System.out.println(array.size());

            /*String filepath = "D:\\ΓΙΩΡΓΟΣ ΣΥΜΕΩΝΙΔΗΣ\\Documents\\6ο ΕΞΑΜΗΝΟ\\ΚΑΤΑΝΕΜΗΜΕΝΑ ΣΥΣΤΗΜΑΤΑ\\MusicStreamer-spotify\\dataset2\\Ancient Winds.mp3";
            MusicFile m = new MusicFile();
            m = mp3extraction(filepath);
            m.printTrack();

            List<MusicFile> list = new ArrayList<MusicFile>();
            list = chunks(m.getMusicFileExtract(),m);

            for (int i = 0; i < list.size(); i++) {
                list.get(i).printTrack();
            }


            System.out.println(list.size());

            createMP3(m,"syme.mp3");*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}