package REST.server.computation;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.ws.rs.Path;
import javax.ws.rs.core.UriBuilder;
import java.util.List;

@Path("/computationservice")
public class ComputationService implements ComputationServiceInterface {

    private int chordAddress;
    private int numberOfFingers;
    private List<Integer> fingers;
    private static String serverPathPrefix;
    private static int serverPort;

    public ComputationService(String serverPathPrefix, int serverPort) {
        ComputationService.serverPathPrefix = serverPathPrefix;
        ComputationService.serverPort = serverPort;
    }

    public ComputationService(int chordAddress, int knownChordNode, int numberOfFingers, String serverPathPrefix, int serverPort) {
        this.chordAddress = chordAddress;
        this.numberOfFingers = numberOfFingers;
        ComputationService.serverPathPrefix = serverPathPrefix;
        ComputationService.serverPort = serverPort;

        addingItselfToChordRing(knownChordNode);

    }

    public void initChordNode() {

    }

    private void addingItselfToChordRing(int knownChordNode) {

    }

    //private void initializeFingerTable()

    @Override
    public String addNode(String address) {
/*        try {
            System.out.println("Got request from client: " + n1 + " " + n2 + " " + op);
            int num1 = Integer.parseInt(n1);
            int num2 = Integer.parseInt(n2);
            int result;
            switch(op) {
                case "add":
                    result = num1 + num2;
                    break;
                case "sub":
                    result = num1 - num2;
                    break;
                case "mul":
                    result = num1 * num2;
                    break;
                case "div":
                    result = num1 / num2;
                    break;
                default:
                    return "\"Error: Could not understand the input parameters\"";
            }

            return Integer.toString(result);
        }
        catch (Exception ex) {
            return "\"Error: Could not understand the input parameters\"";
        }*/
        return null;
    }

    @Override
    public String lookup_successor(String address) {
        return null;
    }

    @Override
    public String lookup_predecessor(String address) {
        return null;
    }

    @Override
    public String update_finger(String finger, String node) {
        return null;
    }

    @Override
    public String getRealSuccessor(String idealAddress) {
        return null;
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
}
