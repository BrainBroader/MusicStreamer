import java.io.*;
import java.net.*;
import java.util.*;

class ActionsForPub extends Thread
{
    final ObjectInputStream dis;
    final ObjectOutputStream dos;
    final Socket s;
    private int PORT;
    private Broker b;
    private String job;


    // Constructor
    public ActionsForPub(Socket s, ObjectInputStream dis, ObjectOutputStream dos, int PORT, Broker b, String job)
    {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
        this.PORT = PORT;
        this.b = b;
        this.job = job;
    }

    @Override
    public void run() {

        try {

            if (job.equals("connect")) {
                ArrayList<String> artists = new ArrayList<>();
                artists = (ArrayList<String>) dis.readObject();
                b.saveArtists(artists);

                ArrayList<String> ips = new ArrayList<>();
                ips = (ArrayList<String>) dis.readObject();
                b.setBrokers_ip(ips);

                ArrayList<Integer> ports = new ArrayList<>();
                ports = (ArrayList<Integer>) dis.readObject();
                b.setBrokers_ports(ports);

                HashMap<String, String> bl = new HashMap<>();
                bl = (HashMap<String, String>) dis.readObject();

                for (Map.Entry me : bl.entrySet()) {
                    b.putBroker_list((String) me.getKey(), (String) me.getValue());
                }

                HashMap<ArrayList<String>, String> ps = new HashMap<>();
                ps = (HashMap<ArrayList<String>, String>) dis.readObject();

                for (Map.Entry m : ps.entrySet()) {
                    b.addPub_servers((ArrayList<String>) m.getKey(), (String) m.getValue());
                }
            } else if (job.equals("reconnect")) {
                HashMap<String, String> bl = new HashMap<>();
                bl = (HashMap<String, String>) dis.readObject();
                b.setBrokers_list(new HashMap<>());
                for (Map.Entry me : bl.entrySet()) {
                    b.putBroker_list((String) me.getKey(), (String) me.getValue());
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                dis.close();
                dos.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}