import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Consumer extends Thread {

    private static ArrayList<Integer> brokers_ports = new ArrayList<>();
    private static ArrayList<String> brokers_ip = new ArrayList<>();
    private static HashMap<String, String> broker_list = new HashMap<>();


    public static void main(String[] args) {

        Consumer c = new Consumer();
        c.connect();
    }

    public void connect() {

        loadPorts("brokers1.txt");

        Random r = new Random();
        int number = r.nextInt(brokers_ip.size());

        ConHandler con_thread = new ConHandler(brokers_ip.get(number), brokers_ports.get(number),this, brokers_ip, brokers_ports);
        con_thread.start();

        try {
            con_thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void loadPorts(String data) {
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
            while(line != null){

                String[] splited = line.split("\\s+");
                String ip = splited[0];
                int port = Integer.parseInt(splited[1]);
                brokers_ports.add(port);
                brokers_ip.add(ip);

                line = reader.readLine();
            }
        } catch (IOException e) {
            System.out.println("Error!!!");
        }
    }

    public void setBroker_list(HashMap<String, String> broker_list) {
        this.broker_list = broker_list;
    }

    public HashMap<String, String> getBroker_list() {
        return this.broker_list;
    }


}


