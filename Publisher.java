import java.io.*; 
import java.net.*; 
import java.util.Scanner; 
  
// Client class 
public class Publisher
{ 
    public static void main(String[] args) throws IOException {
        System.out.print("Enter Port: ");
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        String command = keyboard.readLine();
        int PORT = Integer.parseInt(command);
        System.out.println();

        /*System.out.print("Enter IP: ");
        command = keyboard.readLine();*/
        String IP = "192.168.1.6";
        System.out.println();

        Publisher pub1 = new Publisher();
        pub1.connect(IP, PORT);
    }

    public void connect(String IP, int PORT) {
        try
        {
            Scanner scn = new Scanner(System.in);

            // getting localhost ip 
            InetAddress ip = InetAddress.getByName(IP);

            // establish the connection with server port 5056 
            Socket s = new Socket(ip, PORT);

            // obtaining input and out streams 
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            // the following loop performs the exchange of 
            // information between client and client handler 
            while (true)
            {
                System.out.println(dis.readUTF());
                String tosend = scn.nextLine();
                dos.writeUTF(tosend);

                // If client sends exit,close this connection  
                // and then break from the while loop 
                if(tosend.equals("Exit"))
                {
                    System.out.println("[SERVER] Closing this connection : " + s);
                    s.close();
                    System.out.println("[SERVER] Connection closed");
                    break;
                }

                // printing date or time as requested by client 
                String received = dis.readUTF();
                System.out.println(received);
            }

            // closing resources 
            scn.close();
            dis.close();
            dos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
} 