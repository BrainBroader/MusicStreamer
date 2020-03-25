import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class PubHandler implements Runnable {
    private Socket publisher;
    private BufferedReader in;
    private PrintWriter out;

    public PubHandler(Socket pubSocket) throws IOException {
        publisher = pubSocket;
        in = new BufferedReader(new InputStreamReader(publisher.getInputStream())); 
        out = new PrintWriter(publisher.getOutputStream(), true);
        
    }

    public void run() {
        try {
            while (true) {
                String request = in.readLine();
                out.println(request + "-READ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            out.close();
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
}