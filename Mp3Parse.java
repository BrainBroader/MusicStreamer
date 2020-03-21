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

    public static List<byte[]> chunks(byte[] array) throws IOException {
        List<byte[]> ret = new ArrayList<byte[]>();

        int bytes = array.length;

        for (int i = 0; i < bytes; i = i + 64000) {

            if (i + 64000 >= bytes) {
                int last_bytes = bytes - i;
                int counter = 0;
                for (int j = i; j < j + last_bytes; j++) {
                    byte[] n = new byte[last_bytes];
                    n[counter++] = array[j];
                }
            } else {
                int counter = 0;
                for (int j = i; j < j + 64000; j++) {
                    byte[] n = new byte[64000];
                    n[counter++] = array[j];
                }
            }
        }

        return ret;

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
                array.add(m);

            }
            stream.close();*/

            String filepath = "D:\\ΓΙΩΡΓΟΣ ΣΥΜΕΩΝΙΔΗΣ\\Documents\\6ο ΕΞΑΜΗΝΟ\\ΚΑΤΑΝΕΜΗΜΕΝΑ ΣΥΣΤΗΜΑΤΑ\\ΕΡΓΑΣΙΑ\\dataset1\\Comedy\\A Surprising Encounter.mp3";
            MusicFile m = new MusicFile();
            m = mp3extraction(filepath);
            m.printTrack();

            List<byte[]> list = new ArrayList<byte[]>();
            list = chunks(m.getMusicFileExtract());


            System.out.println(list.size());

            /*byte[] array = m.getMusicFileExtract();
            for (int i = 0; i < array.length; i++) {
                System.out.println(array[i]);
            }*/






        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}