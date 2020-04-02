public class ArtistName {

    private String artistName;

    public ArtistName() { }

    public ArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setName(String artistName) {
        this.artistName = artistName;
    }

    public String getName() {
        return this.artistName;
    }

    public String toString() {
        return "Artist: " + getName();
    }
}