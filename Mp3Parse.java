import MusicFile.MusicFile;
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

        if(mp3file.hasId3v2Tag()){
            ID3v2 tag2 = mp3file.getId3v2Tag();
            artistName = tag2.getArtist();
            trackName = tag2.getTitle();
            albumInfo = tag2.getAlbum();
            genre = tag2.getGenreDescription();
        }

        //creating a musicfile object
        MusicFile song = new MusicFile(trackName,artistName,albumInfo,genre,musicFileExtract);

        return song;
    }

    public static List<MusicFile> chunks(byte[] array, MusicFile music) throws IOException {
        List<MusicFile> ret = new ArrayList<MusicFile>();

        int bytes = array.length;
        int loops = 0;
        if (bytes % 512000 == 0) {
            loops = bytes / 512000;
        } else {
            loops = (bytes / 512000) + 1;
        }


        for (int i = 0; i < loops; i++) {

            if (i == loops - 1) {

                byte[] a = new byte[array.length - i * 512000];
                for (int j = 0; j < a.length; j++) {
                    a[j] = array[(i * 512000) + j];
                }
                MusicFile m = new MusicFile(music.getTrackName(),music.getArtistName(),music.getAlbumInfo(),music.getGenre(),a);
                ret.add(m);

            } else {
                byte[] a = new byte[512000];
                for (int j = 0; j < 512000; j++) {
                    a[j] = array[(i * 512000) + j];
                }
                MusicFile m = new MusicFile(music.getTrackName(),music.getArtistName(),music.getAlbumInfo(),music.getGenre(),a);
                ret.add(m);
            }
        }
        return ret;
    }

    public MusicFile reproduce(List<MusicFile> list) {

        int bounds = (list.size() - 1) * 512000;
        int last = list.get(list.size()-1).getMusicFileExtract().length;
        bounds = bounds + last;


        byte[] array = new byte[bounds];

        int counter = 0;
        for (int i = 0; i < list.size(); i++) {
            byte[] chunk = list.get(i).getMusicFileExtract();
            for (int j = 0; j < chunk.length; j++) {
                array[counter] = chunk[j];
                counter++;
            }
        }

        MusicFile musicfile = new MusicFile();
        musicfile.setTrackName(list.get(0).getTrackName());
        musicfile.setArtistName(list.get(0).getArtistName());
        musicfile.setAlbumInfo(list.get(0).getAlbumInfo());
        musicfile.setGenre(list.get(0).getGenre());
        musicfile.setMusicFileExtract(array);

        return musicfile;
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
    }
}