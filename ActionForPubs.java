import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ActionForPubs extends Thread {
    ObjectInputStream in;
    ObjectOutputStream out;


    public ActionForPubs(Socket connection) {
        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            try {
                MusicFile request = (MusicFile) in.readObject();
                System.out.println("Message received.");
                request.printTrack();

                Mp3Parse prse = new Mp3Parse();
                prse.createMP3(request,"syme2.mp3");

                System.out.println("Job's done!");
                //out.writeObject(request);
               // System.out.println("Object returning...");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

}