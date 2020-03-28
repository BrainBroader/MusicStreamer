import java.io.*; 
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

class ActionsForPub extends Thread
{
    ObjectInputStream dis;
    ObjectOutputStream dos;
    final Socket s;
    private int PORT;
      
  
    // Constructor 
    public ActionsForPub(Socket s, int PORT)
    { 
        this.s = s;
        try {
            ObjectInputStream dis = new ObjectInputStream(s.getInputStream());
            ObjectOutputStream dos = new ObjectOutputStream(s.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.PORT = PORT;
    } 
  
    @Override
    public void run() {

        String received;
        String toreturn;

        try {

             ArrayList<String> artists = new ArrayList<>();
             artists = (ArrayList<String>) dis.readObject();

             for (int i = 0; i < artists.size(); i++) {
                 System.out.println(artists.get(i));
             }






        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }








        /*while (true) {
            try {

                // Ask user what he wants
                dos.writeUTF("> ");

                // receive the answer from client
                received = dis.readUTF();
                System.out.println("[Publisher "+PORT+"] " + received);

                if (received.equals("exit")) {
                    System.out.println("Publisher " + this.s + " sends exit...");
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
        }*/
    }
}