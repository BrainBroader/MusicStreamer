import java.util.List;

public class Node extends Thread {

    public List<Broker> brokers;

    public Node() {

    }

    public Node(List<Broker> brokers) {
        this.brokers = brokers;
    }

    public void init(int i) {

    }

    public List<Broker> getBrokers() {
        return this.brokers;
    }

    public void connect() {

    }

    public void discconnect() {

    }

    public void updateNode() {

    }

}