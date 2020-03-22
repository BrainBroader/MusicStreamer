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
                ret.add(a);
            } else {
                byte[] a = new byte[64000];
                for (int j = 0; j < 64000; j++) {
                    a[j] = array[(i * 64000) + j];
                }
                ret.add(a);
            }

        }

        return ret;

    }

    public static void main(String args[])
    {

        try {
            ArrayList<MusicFile> array = new ArrayList<MusicFile>();
            String filepath = "D:\\ΓΙΩΡΓΟΣ ΣΥΜΕΩΝΙΔΗΣ\\Documents\\6ο ΕΞΑΜΗΝΟ\\ΚΑΤΑΝΕΜΗΜΕΝΑ ΣΥΣΤΗΜΑΤΑ\\ΕΡΓΑΣΙΑ\\dataset1\\Comedy";

            Path dir = FileSystems.getDefault().getPath(filepath);
            DirectoryStream<Path> stream = Files.newDirectoryStream( dir );
            for (Path path : stream) {

                //System.out.println(path.getFileName());
                String p = filepath + "/" +(path.getFileName()).toString();
                MusicFile m = new MusicFile();
                m = mp3extraction(p);
                List<byte[]> list = new ArrayList<byte[]>();
                list = chunks(m.getMusicFileExtract());
                m.printTrack();
                //array.add(m);

            }
            stream.close();

            /*String filepath = "D:\\ΓΙΩΡΓΟΣ ΣΥΜΕΩΝΙΔΗΣ\\Documents\\6ο ΕΞΑΜΗΝΟ\\ΚΑΤΑΝΕΜΗΜΕΝΑ ΣΥΣΤΗΜΑΤΑ\\ΕΡΓΑΣΙΑ\\dataset1\\Comedy\\A Surprising Encounter.mp3";
            MusicFile m = new MusicFile();
            m = mp3extraction(filepath);
            m.printTrack();

            List<byte[]> list = new ArrayList<byte[]>();
            list = chunks(m.getMusicFileExtract());


            System.out.println(list.size());

            byte[] array = m.getMusicFileExtract();
            for (int i = 0; i < array.length; i++) {
                System.out.println(array[i]);
            }*/






        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}