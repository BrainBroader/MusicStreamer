import java.io.Serializable;

public class MusicFile implements Serializable {

    private String trackName;
    private String artistName;
    private String albumInfo;
    private String genre;
    private byte[] musicFileExtract;

    public MusicFile() {

    }

    public MusicFile (String trackName, String artistName, String albumInfo, String genre, byte[] musicFileExtract) {
        this.trackName = trackName;
        this.artistName = artistName;
        this.albumInfo = albumInfo;
        this.genre = genre;
        this.musicFileExtract = musicFileExtract;

    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getTrackName() {
        return this.trackName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getArtistName() {
        return this.artistName;
    }

    public void setAlbumInfo(String albumInfo) {
        this.albumInfo = albumInfo;
    }

    public String getAlbumInfo() {
        return this.albumInfo;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getGenre() {
        return this.genre;
    }

    public void setMusicFileExtract(byte[] musicFileExtract) {
        this.musicFileExtract = musicFileExtract;
    }

    public byte[] getMusicFileExtract() {
        return this.musicFileExtract;
    }

    public void printTrack() {
        System.out.println("Title: " + getTrackName() + "\nArtist: " + getArtistName() + "\nAlbum: " + getAlbumInfo() + "\nGenre: " + getGenre()+ "\n" + getMusicFileExtract().length + " byte");
        System.out.println("----------------------------------------");
    }


}