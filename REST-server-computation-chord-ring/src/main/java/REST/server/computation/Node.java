package REST.server.computation;

public class Node {
    private final int numberOfFingers;
    public Finger[] fingers;
    private final int nodeId;

    // the previous node on the identifier circle
    private int predecessor;

    public Node(int numberOfFingers, int nodeId) {
        this.numberOfFingers = numberOfFingers;
        this.nodeId = nodeId;
        this.fingers = new Finger[numberOfFingers];
        for (int i = 0; i < numberOfFingers; i++) {
            this.fingers[i] = new Finger();
            this.fingers[i].setStart((nodeId + (int)Math.pow(2, i)) % (int)Math.pow(2, numberOfFingers));
        }

        this.fingers[numberOfFingers - 1].setEnd(this.fingers[0].getStart());
        for (int i = 0; i < numberOfFingers - 1; i++) {
            this.fingers[i].setEnd(this.fingers[i + 1].getStart());
        }

        System.out.println("Node: " + this);
    }

    public int getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(int predecessor) {
        this.predecessor = predecessor;
    }

    public int getSuccessor() {
        return fingers[0].getNode();
    }

    public int getNodeId() {
        return nodeId;
    }

    public int getNumberOfFingers() {
        return numberOfFingers;
    }

    @Override
    public String toString() {
        String fingersAsString = "";
        for (int i = 0; i < numberOfFingers - 1; i++) {
            fingersAsString += "[" + fingers[i].getStart() + "," + fingers[i+1].getStart() + "), node: " + fingers[i].getNode() + System.lineSeparator();
        }

        fingersAsString += "[" + fingers[numberOfFingers - 1].getStart() + "," + fingers[0].getStart() + "), node: " + fingers[numberOfFingers - 1].getNode() + System.lineSeparator();

        return "Node address: " + nodeId + System.lineSeparator() +
                "Successor: " + getSuccessor() + System.lineSeparator() +
                "Predecessor: " + getPredecessor() + System.lineSeparator() +
                "FingerTable:" + System.lineSeparator() + fingersAsString;
    }
}

class Finger {
    // finger[k].start = n + 2^(k) mod 2^m, 0 <= k < m
    private int start;

    // [finger[k].start, finger[k+1].start)
    private int end;

    // first node >= n.finger[k].start
    private int node;

    public Finger() {
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

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
