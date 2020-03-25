public class  Value {

    private MusicFile musicFile;

    public Value() {

    }

    public Value(MusicFile musicFile) {

        this.musicFile = musicFile;
    }

    public void setMusicFile(MusicFile musicFile) {
        this.musicFile = musicFile;
    }

    public MusicFile getMusicFile() {
        return this.musicFile;
    }

    public String toString() {
        return getMusicFile().toString();
    }


}