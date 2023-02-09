package REST.server.computation;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.ws.rs.Path;
import java.net.UnknownHostException;
import java.util.HashMap;

@Path("/computationservice")
public class ComputationService implements ComputationServiceInterface {
    private static String serverPathPrefix;
    private static int serverPort;
    private final Node node;
    private static HashMap<Integer, String> myHashMap = new HashMap<>();

    public ComputationService(int nodeId, boolean isFirstNode, int knownChordNode, int numberOfFingers, String serverPathPrefix, int serverPort) {
        ComputationService.serverPathPrefix = serverPathPrefix;
        ComputationService.serverPort = serverPort;
        this.node = new Node(numberOfFingers, nodeId);
        System.out.println("Nodetest:");
        System.out.println("Node: " + this.node);
        join(knownChordNode, isFirstNode);
    }

    private void join(int n, boolean isFirstNode) {
        System.out.println(node.getNodeId() + " calls join with: " + n + ", " + isFirstNode);
        if (isFirstNode) {
            for (int i = 0; i < node.getNumberOfFingers(); i++) {
                node.fingers[i].setNode(node.getNodeId());
            }

            node.setPredecessor(node.getNodeId());
        }
        else {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            init_finger_table(n);
            update_others();
        }
    }

    private void init_finger_table(int n) {
        try {
            System.out.println(node.getNodeId() + " calls init_finger_table with: " + n);
            ComputationServiceInterface proxy = createProxyObject(n);
            String proxyReturn = proxy.find_successor(node.fingers[0].getStart());
            node.fingers[0].setNode(Integer.parseInt(proxyReturn));

            proxy = createProxyObject(node.getSuccessor());
            node.setPredecessor(Integer.parseInt(proxy.getPredecessor()));
            proxy.setPredecessor(node.getNodeId());
            for (int i = 0; i < node.getNumberOfFingers() - 1; i++) {
                if (isInInterval(node.fingers[i + 1].getStart(), false, n, true, node.fingers[i].getNode())) {
                    node.fingers[i + 1].setNode(node.fingers[i].getNode());
                }
                else {
                    proxy = createProxyObject(n);
                    proxyReturn = proxy.find_successor(node.fingers[i + 1].getStart());
                    node.fingers[i + 1].setNode(Integer.parseInt(proxyReturn));
                }
            }
            return;
        }
        catch (Exception e) {
            System.err.println("Error in init_finger_table.");
            e.printStackTrace();
            return;
        }
    }

    private void update_others() {
        try {
            System.out.println(node.getNodeId() + " calls update_others");
            for (int i = 0; i < node.getNumberOfFingers(); i++) {
                int p = Integer.parseInt(find_predecessor(node.getNodeId() - (int)Math.pow(2, i)));
                ComputationServiceInterface proxy = createProxyObject(p);
                proxy.update_finger_table(node.getNodeId(), i);
            }
        }
        catch (Exception e) {
            System.err.println("Error in update_others." + e);
            e.printStackTrace();
        }
    }

    @Override
    public String find_successor(int id) {
        try {
            System.out.println(node.getNodeId() + " calls find_successor with: " + id);
            String proxyReturn = find_predecessor(id);
            ComputationServiceInterface proxy = createProxyObject(Integer.parseInt(proxyReturn));
            return proxy.getSuccessor();
        }
        catch (Exception e) {
            System.err.println("Error in find_successor." + e);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String find_predecessor(int id) {
        try {
            System.out.println(node.getNodeId() + " calls find_predecessor with: " + id);
            int tempNodeId = node.getNodeId();
            ComputationServiceInterface proxy = createProxyObject(tempNodeId);
            while (!isInInterval(id, true, tempNodeId, false, Integer.parseInt(proxy.getSuccessor()))) {
                tempNodeId = Integer.parseInt(proxy.closest_preceding_finger(id));
                proxy = createProxyObject(tempNodeId);
            }

            return Integer.toString(tempNodeId);
        }
        catch (Exception e) {
            System.err.println("Error in find_predecessor." + e);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String closest_preceding_finger(int id) {
        System.out.println(node.getNodeId() + " calls closest_preceding_finger with: " + id);
        for (int i = node.getNumberOfFingers() - 1; i >= 0 ; i--) {
            if (isInInterval(node.fingers[i].getNode(), true, node.getNodeId(), true, id)) {
                return Integer.toString(node.fingers[i].getNode());
            }
        }

        return Integer.toString(node.getNodeId());
    }

    @Override
    public String getSuccessor() {
        System.out.println(node.getNodeId() + " calls getSuccessor");
        return Integer.toString(node.getSuccessor());
    }

    @Override
    public String getPredecessor() {
        System.out.println(node.getNodeId() + " calls getPredecessor");
        return Integer.toString(node.getPredecessor());
    }

    @Override
    public void setPredecessor(int id) {
        System.out.println(node.getNodeId() + " calls setPredecessor with: " + id);
        node.setPredecessor(id);
    }

    @Override
    public void update_finger_table(int s, int i) {
        System.out.println(node.getNodeId() + " calls update_finger_table with: " + s + ", " + i);

        try {
            if (isInInterval(s, false, node.getNodeId(), true, node.fingers[i].getNode())) {
                node.fingers[i].setNode(s);
                int p = node.getPredecessor();
                ComputationServiceInterface proxy = createProxyObject(p);
                proxy.update_finger_table(s, i);
            }
        }
        catch (Exception e) {
            System.err.println("Error in update_finger_table." + e);
            e.printStackTrace();
        }
    }

    @Override
    public String sendMessage(String key, String message) {
        System.out.println(node.getNodeId() + " calls sendMessage with key: " + key + " and message: " + message);
        try {
            int intKey = Integer.parseInt(key);
            if (isInInterval(intKey, false, node.getPredecessor(), true, node.getNodeId())) {
                // Node found
                System.out.println(node.getNodeId() + " got message: " + message);
            }
            else {
                String foundNode = find_successor(intKey);
                ComputationServiceInterface proxy = createProxyObject(Integer.parseInt(foundNode));
                return proxy.sendMessage(key, message);
            }
        }
        catch (Exception e) {
            System.err.println("Error in sendMessage." + e);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String add(String value) {
        System.out.println(node.getNodeId() + " calls add with: " + value);
        try {
            int key = createHashCode(value);
            if (isInInterval(key, false, node.getPredecessor(), true, node.getNodeId())) {
                // Node found
                System.out.println(node.getNodeId() + " adds value: " + value);
                myHashMap.put(key, value);
            }
            else {
                String foundNode = find_successor(key);
                ComputationServiceInterface proxy = createProxyObject(Integer.parseInt(foundNode));
                return proxy.add(value);
            }
        }
        catch (Exception e) {
            System.err.println("Error in add." + e);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String get(String key) {
        System.out.println(node.getNodeId() + " calls get with: " + key);

        try {
            int intKey = Integer.parseInt(key);
            if (isInInterval(intKey, false, node.getPredecessor(), true, node.getNodeId())) {
                // Node found
                System.out.println(node.getNodeId() + " get value of key: " + intKey);
                if (myHashMap.containsKey(intKey)) {
                    return myHashMap.get(intKey);
                }
                else {
                    return node.getNodeId() + " does not contain key: " + intKey;
                }
            }
            else {
                String foundNode = find_successor(intKey);
                ComputationServiceInterface proxy = createProxyObject(Integer.parseInt(foundNode));
                return proxy.get(key);
            }
        }

        catch (Exception e) {
            System.err.println("Error in get." + e);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String remove(String key) {
        System.out.println(node.getNodeId() + " calls remove with: " + key);

        try {
            int intKey = Integer.parseInt(key);
            if (isInInterval(intKey, false, node.getPredecessor(), true, node.getNodeId())) {
                // Node found
                System.out.println(node.getNodeId() + " removes key: " + intKey);
                myHashMap.remove(intKey);
            }
            else {
                String foundNode = find_successor(intKey);
                ComputationServiceInterface proxy = createProxyObject(Integer.parseInt(foundNode));
                return proxy.remove(key);
            }
        }

        catch (Exception e) {
            System.err.println("Error in remove." + e);
            e.printStackTrace();
        }

        return null;
    }

    public ComputationServiceInterface createProxyObject(int id) throws UnknownHostException {
        // Create client
        //String path = "http://chord_node" + id + "_1:" + serverPort + serverPathPrefix + "/" + id;
        //String path = "http://localhost:" + (8081 + id) + serverPathPrefix + "/";
        //String path = "http://node" + id + serverPathPrefix + "/";
        String path = "http://node" + id + ":8989" + serverPathPrefix;

        //String path = "http://" + ip.getHostAddress() + ":8989" + serverPathPrefix;


        System.out.println(node.getNodeId() + " calls createProxyObject with path: " + path);
        ResteasyClient client = new ResteasyClientBuilder().build();
        //ResteasyWebTarget target = client.target(UriBuilder.fromPath("http://localhost:" + serverPort + serverPathPrefix + "/" + id));
        //ResteasyWebTarget target = client.target(UriBuilder.fromPath(path));
        ResteasyWebTarget target = client.target(path);
        return target.proxy(ComputationServiceInterface.class);
    }

    private boolean isInInterval(int value, boolean includingStart, int start, boolean includingEnd, int end) {
        boolean returnValue = false;
        if (start <= end) {
            returnValue = includingStart ? start <= value : start < value && (includingEnd ? value <= end : value < end);
        }
        else {
            if (includingStart ? start <= value : start < value || includingEnd ? value <= end : value < end) {
                returnValue = true;
            }
        }

        String interval = includingStart ? "(" : "[" + start + "," + end + (includingEnd ? ")" : "]");
        System.out.println(node.getNodeId() + " calls isInInterval with: " + value + " is element of " + interval + ", returns" + returnValue);
        return returnValue;
    }

    private int createHashCode(String value) {
        int key = value.hashCode() % (int)Math.pow(2, node.getNumberOfFingers());
        System.out.println(node.getNodeId() + " calls createHashCode returns: " + key);
        return key;
    }
}
