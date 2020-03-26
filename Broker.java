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
                Thread t = new ActionsForPub(s, dis, dos,PORT);
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

    public static int loadPorts(String data) {
        int port = -1;
        File f = null;
        BufferedReader reader = null;
        String line;

        try {
            f = new File(data);
        } catch (NullPointerException e) {
            System.err.println("File not found.");

        } try {
            reader = new BufferedReader(new FileReader(f));
        } catch (FileNotFoundException e) {
            System.err.println("Error opening file!");

        } try {
            line = reader.readLine();

            String[] splited = line.split("\\s+");
            port = Integer.parseInt(splited[0]);


            Scanner fileScanner = new Scanner(f);
            fileScanner.nextLine();

            FileWriter fileStream = new FileWriter("brokers2.txt");
            BufferedWriter out = new BufferedWriter(fileStream);
            while(fileScanner.hasNextLine()) {
                String next = fileScanner.nextLine();
                if(next.equals("\n"))
                    out.newLine();
                else
                    out.write(next);
                out.newLine();
            }
            out.close();



        } catch (IOException e) {
            System.out.println("Error!!!");
        }
        return port;
    }

    public static void main(String[] args) throws IOException {

        /*System.out.print("Enter Port: ");
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        String command = keyboard.readLine();
        int PORT = Integer.parseInt(command);
        System.out.println();*/

        int PORT = loadPorts("brokers2.txt");

        new Broker().openServer(PORT);

    }
} 