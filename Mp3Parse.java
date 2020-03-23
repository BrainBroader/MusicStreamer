import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

//Mp3 extraction packages
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;


public class Mp3Parse {

    public Mp3Parse() {

    }

    public static MusicFile mp3extraction(String path) throws Exception, IOException, SAXException, TikaException {

        String trackName = null;
        String artistName = null;
        String albumInfo = null;
        String genre = null;
        byte[] musicFileExtract;

        //detecting the file type
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();

        File track = new File(path);
        FileInputStream inputstream = new FileInputStream(track);
        ParseContext pcontext = new ParseContext();

        //Mp3 parser
        Mp3Parser  Mp3Parser = new  Mp3Parser();
        Mp3Parser.parse(inputstream, handler, metadata, pcontext);


        musicFileExtract = Files.readAllBytes(Paths.get(path));

        String[] metadataNames = metadata.names();

        for(String name : metadataNames) {
            if(name.equals("xmpDM:genre")){
                genre = metadata.get(name);
            }
            else if (name.equals("xmpDM:artist")){
                artistName = metadata.get(name);
            }
            else if (name.equals("xmpDM:album")){
                albumInfo =  metadata.get(name);
            }
            else if(name.equals("title")){
                trackName =  metadata.get(name);
            } else {
                continue;
            }

        }
        inputstream.close();

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
            /*ArrayList<MusicFile> array = new ArrayList<MusicFile>();
            String filepath = "D:\\ΓΙΩΡΓΟΣ ΣΥΜΕΩΝΙΔΗΣ\\Documents\\6ο ΕΞΑΜΗΝΟ\\ΚΑΤΑΝΕΜΗΜΕΝΑ ΣΥΣΤΗΜΑΤΑ\\ΕΡΓΑΣΙΑ\\dataset1\\Comedy";

            Path dir = FileSystems.getDefault().getPath(filepath);
            DirectoryStream<Path> stream = Files.newDirectoryStream( dir );
            for (Path path : stream) {

                //System.out.println(path.getFileName());
                String p = filepath + "/" +(path.getFileName()).toString();
                MusicFile m = new MusicFile();
                m = mp3extraction(p);
                List<MusicFile> list = new ArrayList<MusicFile>();
                list = chunks(m.getMusicFileExtract(), m);

                //createMP3(m, path.getFileName().toString());
                //array.add(m);

            }
            stream.close();*/

            String filepath = "D:\\ΓΙΩΡΓΟΣ ΣΥΜΕΩΝΙΔΗΣ\\Documents\\6ο ΕΞΑΜΗΝΟ\\ΚΑΤΑΝΕΜΗΜΕΝΑ ΣΥΣΤΗΜΑΤΑ\\ΕΡΓΑΣΙΑ\\dataset1\\Comedy\\A Surprising Encounter.mp3";
            MusicFile m = new MusicFile();
            m = mp3extraction(filepath);
            m.printTrack();

            List<MusicFile> list = new ArrayList<MusicFile>();
            list = chunks(m.getMusicFileExtract(),m);

            for (int i = 0; i < list.size(); i++) {
                list.get(i).printTrack();
            }


            System.out.println(list.size());

            createMP3(m,"syme.mp3");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}