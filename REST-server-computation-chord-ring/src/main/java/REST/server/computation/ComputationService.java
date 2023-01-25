package REST.server.computation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/computationservice")
public class ComputationService implements ComputationServiceInterface {

    private int chordAddress;
    private int numberOfFingers;
    private List<Integer> fingers;

    public ComputationService(int chordAddress, int knownChordNode, int numberOfFingers) {
        this.chordAddress = chordAddress;
        this.numberOfFingers = numberOfFingers;
        fingers = new ArrayList<>();
        addingItselfToChordRing(knownChordNode);

    }

    private void addingItselfToChordRing(int knownChordNode) {

    }





    @GET
    @Path("/calculate")
    @Produces({MediaType.APPLICATION_JSON})
    public String calculate(@QueryParam("n1") String n1, @QueryParam("n2") String n2, @QueryParam("op") String op) {
        try {
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
        }
    }

    @Override
    public String addNode(String address) {
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
}
