package REST.server.computation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/computationservice")
public interface ComputationServiceInterface {

    @GET
    @Path("/lookup")
    @Produces({ MediaType.APPLICATION_JSON })
    // This is used for adding a node with the given address.
    // It will change the successor of one node and the predecessor of another one.
    // n.join(n')
    public String addNode(@QueryParam("address") String address);

    @GET
    @Path("/lookup_successor")
    @Produces({ MediaType.APPLICATION_JSON })
    // This is used to look up the successor of an address.
    // n.find_successor(id)
    public String lookup_successor(@QueryParam("address") String address);

    @GET
    @Path("/lookup_predecessor")
    @Produces({ MediaType.APPLICATION_JSON })
    // This is used to look up the predecessor of an address.
    // n.find_predecessor(id)
    public String lookup_predecessor(@QueryParam("address") String address);

    @GET
    @Path("/update_finger")
    @Produces({ MediaType.APPLICATION_JSON })
    // This is used to update the position i of the fingers.
    // Finger is the finger which should get updated.
    // Node is the node which has been added.
    public String update_finger(@QueryParam("finger") String finger, @QueryParam("node") String node);

    @GET
    @Path("/realSuccessor")
    @Produces({ MediaType.APPLICATION_JSON })
    // For adding a node it is required to create a finger table.
    // Therefore, sending the ideal address to get back the successor of the address, which is the real address for the finger table.
    public String getRealSuccessor(@QueryParam("idealAddress") String idealAddress);

    @GET
    @Path("/sendMessage")
    @Produces({ MediaType.APPLICATION_JSON })
    public String sendMessage(@QueryParam("key") String key, @QueryParam("message") String message);
}
