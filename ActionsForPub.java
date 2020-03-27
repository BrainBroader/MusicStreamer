import java.io.*; 
import java.net.*;
import java.util.Scanner;

class ActionsForPub extends Thread
{
    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket s;
    private int PORT;
      
  
    // Constructor 
    public ActionsForPub(Socket s, DataInputStream dis, DataOutputStream dos,int PORT)
    { 
        this.s = s; 
        this.dis = dis; 
        this.dos = dos;
        this.PORT = PORT;
    } 
  
    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        String received;
        String toreturn;
        while (true) {
            try {

                // Ask user what he wants
                dos.writeUTF("> ");

                // receive the answer from client
                received = dis.readUTF();
                System.out.println("[Client "+PORT+"] " + received);

                if (received.equals("exit")) {
                    System.out.println("Client " + this.s + " sends exit...");
                    System.out.println("Closing this connection.");
                    this.s.close();
                    System.out.println("Connection closed");
                    break;
                }

                toreturn = sc.nextLine();
                toreturn = "[SERVER "+PORT+"] " + toreturn;
                //System.out.println(toreturn);
                dos.writeUTF(toreturn);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}