import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

//Mp3 extraction packages
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;




public class Mp3Parse {

    //variables
    private String trackName = null;
    private String artistName = null;
    private String albumInfo = null;
    private String genre = null;
    private byte[] musicFileExtract;
    MusicFile song;


    public MusicFile mp3extraction() throws Exception, IOException, SAXException, TikaException {

        //detecting the file type
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        FileInputStream inputstream = new FileInputStream(new File("C:\\Users\\MrAG99\\IdeaProjects\\Beatlok\\One Step Closer.mp3" ));
        ParseContext pcontext = new ParseContext();

        //Mp3 parser
        Mp3Parser  Mp3Parser = new  Mp3Parser();
        Mp3Parser.parse(inputstream, handler, metadata, pcontext);

        //maybe it will need changes
        musicFileExtract = inputstream.readAllBytes();

        System.out.println("Metadata of the document:");
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
            }else{continue;}

        }
        inputstream.close();
        System.out.println("g "+genre+" t "+trackName+" a "+albumInfo+" ar "+artistName);

        //creating a musicfile object
        song = new MusicFile(trackName,artistName,albumInfo,genre,musicFileExtract);

        return song;
    }
}