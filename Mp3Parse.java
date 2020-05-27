import MusicFile.MusicFile;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import java.nio.file.*;

public class Mp3Parse {

    public Mp3Parse() {}

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

        long duration = mp3file.getLengthInMilliseconds();
        //long duration = mp3file.getLengthInSeconds();

        //creating a musicfile object
        MusicFile song = new MusicFile(trackName,artistName,albumInfo,genre,duration,musicFileExtract);

        return song;
    }
}