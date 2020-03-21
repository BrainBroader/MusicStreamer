import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

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
        FileInputStream inputstream = new FileInputStream(new File(path));
        ParseContext pcontext = new ParseContext();

        //Mp3 parser
        Mp3Parser  Mp3Parser = new  Mp3Parser();
        Mp3Parser.parse(inputstream, handler, metadata, pcontext);

        //maybe it will need changes
        musicFileExtract = inputstream.readAllBytes();

        //System.out.println("Metadata of the document:");
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
                array.add(m);

            }
            stream.close();


            for (MusicFile a : array) {
                
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}