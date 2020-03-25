import java.io.*; 
import java.text.*; 
import java.util.*; 
import java.net.*; 
  
// Server class 
public class Broker
{
    private static ArrayList<ActionsForPub> publishers = new ArrayList<ActionsForPub>();


    public void openServer(int PORT) throws IOException {

        // server is listening on port 5056 
        ServerSocket ss = new ServerSocket(PORT);
        System.out.println("Server started.");
        System.out.println("Waiting for a Publisher connection...\n");
          
        // running infinite loop for getting 
        // client request 
        while (true)  
        { 
            Socket s = null; 
              
            try 
            { 
                // socket object to receive incoming client requests 
                s = ss.accept(); 
                  
                System.out.println("A new publisher connected. : " + s);
                  
                // obtaining input and out streams 
                DataInputStream dis = new DataInputStream(s.getInputStream()); 
                DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
                  
                System.out.println("Assigning new thread for this publisher.");
  
                // create a new thread object 
                Thread t = new ActionsForPub(s, dis, dos);
                publishers.add((ActionsForPub)t);
                // Invoking the start() method 
                t.start(); 
                  
            } 
            catch (Exception e){ 
                s.close(); 
                e.printStackTrace(); 
            } 
        } 
    }

    public static void main(String[] args) throws IOException {

        System.out.print("Enter Port: ");
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        String command = keyboard.readLine();
        int PORT = Integer.parseInt(command);
        System.out.println();

        new Broker().openServer(PORT);

    }
} 