import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;



public class Publisher extends Thread {

    public Publisher() {

    }

    public void run() {

        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        try {

            requestSocket = new Socket("127.0.0.1", 4321);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            //Create mp3
            String filepath = "D:\\ΓΙΩΡΓΟΣ ΣΥΜΕΩΝΙΔΗΣ\\Documents\\6ο ΕΞΑΜΗΝΟ\\ΚΑΤΑΝΕΜΗΜΕΝΑ ΣΥΣΤΗΜΑΤΑ\\ΕΡΓΑΣΙΑ\\dataset1\\Comedy\\A Surprising Encounter.mp3";
            Mp3Parse parse = new Mp3Parse();
            MusicFile m = new MusicFile();
            m = parse.mp3extraction(filepath);
            m.printTrack();

            //Make chunks
            List<MusicFile> list = new ArrayList<MusicFile>();
            list = parse.chunks(m.getMusicFileExtract(),m);
            System.out.println(list.size());


            System.out.println("Mp3 Song Created.");
            for (int i = 0; i < list.size(); i++) {
                out.writeObject(list.get(i));
                System.out.println("Message sent.");
            }

        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (TikaException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close(); out.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }

    public static void main(String args[]) {

        Publisher pub = new Publisher();
        pub.start();

    }
}