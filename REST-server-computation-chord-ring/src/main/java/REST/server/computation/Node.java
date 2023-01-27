package REST.server.computation;

public class Node {
    private final int numberOfFingers;
    private Finger fingers[];

    // the next node on the identifier circle; finger[0].node
    private int successor;

    // the previous node on the identifier circle
    private int predecessor;

    public Node(int numberOfFingers) {
        this.numberOfFingers = numberOfFingers;
        this.fingers = new Finger[numberOfFingers];
    }


}

class Finger {
    // finger[k].start = n + 2^(k) mod 2^m, 0 <= k < m
    private int start;

    // [finger[k].start, finger[k+1].start)
    private int interval;

    // first node >= n.finger[k].start
    private int node;

    public Finger(int start, int node, int interval) {
        this.start = start;
        this.interval = interval;
        this.node = node;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getNode() {
        return node;
    }

    public void setNode(int node) {
        this.node = node;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
}
