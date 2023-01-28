package REST.server.computation;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.ws.rs.Path;
import javax.ws.rs.core.UriBuilder;

@Path("/computationservice")
public class ComputationService implements ComputationServiceInterface {
    private static String serverPathPrefix;
    private static int serverPort;
    private final Node node;

    public ComputationService(int nodeId, boolean isFirstNode, int knownChordNode, int numberOfFingers, String serverPathPrefix, int serverPort) {
        ComputationService.serverPathPrefix = serverPathPrefix;
        ComputationService.serverPort = serverPort;
        this.node = new Node(numberOfFingers, nodeId);
        join(knownChordNode, isFirstNode);
    }

    private void join(int n, boolean isFirstNode) {
        if (isFirstNode) {
            for (int i = 0; i < node.getNumberOfFingers(); i++) {
                node.fingers[i].setNode(node.getNodeId());
            }

            node.setPredecessor(node.getNodeId());
        }
        else {
            init_finger_table(n);
            update_others();
        }
    }

    private void init_finger_table(int n) {
        try {
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
            System.err.println("Error in init_finger_table." + e);
            return;
        }
    }

    private void update_others() {
        try {
            for (int i = 0; i < node.getNumberOfFingers(); i++) {
                int p = Integer.parseInt(find_predecessor(node.getNodeId() - (int)Math.pow(2, i)));
                ComputationServiceInterface proxy = createProxyObject(p);
                proxy.update_finger_table(node.getNodeId(), i);
            }
        }
        catch (Exception e) {
            System.err.println("Error in update_others." + e);
        }
    }

    @Override
    public String find_successor(int id) {
        try {
            String proxyReturn = find_predecessor(id);
            ComputationServiceInterface proxy = createProxyObject(Integer.parseInt(proxyReturn));
            return proxy.getSuccessor();
        }
        catch (Exception e) {
            System.err.println("Error in find_successor." + e);
            return null;
        }
    }

    @Override
    public String find_predecessor(int id) {
        try {
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
            return null;
        }
    }

    @Override
    public String closest_preceding_finger(int id) {
        for (int i = node.getNumberOfFingers() - 1; i >= 0 ; i--) {
            if (isInInterval(node.fingers[i].getNode(), true, node.getNodeId(), true, id)) {
                return Integer.toString(node.fingers[i].getNode());
            }
        }

        return Integer.toString(node.getNodeId());
    }

    @Override
    public String getSuccessor() {
        return Integer.toString(node.getSuccessor());
    }

    @Override
    public String getPredecessor() {
        return Integer.toString(node.getPredecessor());
    }

    @Override
    public void setPredecessor(int id) {
        node.setPredecessor(id);
    }

    @Override
    public void update_finger_table(int s, int i) {
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
        }
    }

    @Override
    public String sendMessage(String key, String message) {
        return null;
    }

    public ComputationServiceInterface createProxyObject(int node) {
        // Create client
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(UriBuilder.fromPath("http://localhost:" + serverPort + serverPathPrefix + "/" + node));
        return target.proxy(ComputationServiceInterface.class);
    }

    private boolean isInInterval(int value, boolean includingStart, int start, boolean includingEnd, int end) {
        if (start <= end) {
            return includingStart ? start <= value : start < value && (includingEnd ? value <= end : value < end);
        }
        else {
            if (includingStart ? start <= value : start < value || includingEnd ? value <= end : value < end) {
                return true;
            }
        }

        return false;
    }
}
