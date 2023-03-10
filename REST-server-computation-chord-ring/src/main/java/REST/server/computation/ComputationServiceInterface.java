package REST.server.computation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/computationservice")
public interface ComputationServiceInterface {
    @GET
    @Path("/find_successor")
    @Produces({ MediaType.APPLICATION_JSON })
    // This is used to look up the successor of an address.
    public String find_successor(@QueryParam("id") int id);

    @GET
    @Path("/find_predecessor")
    @Produces({ MediaType.APPLICATION_JSON })
    // This is used to look up the predecessor of an address.
    public String find_predecessor(@QueryParam("id") int id);

    @GET
    @Path("/closest_preceding_finger")
    @Produces({ MediaType.APPLICATION_JSON })
    // Find the closest preceding finger
    public String closest_preceding_finger(@QueryParam("id") int id);

    @GET
    @Path("/getSuccessor")
    @Produces({ MediaType.APPLICATION_JSON })
    // For adding a node it is required to create a finger table.
    // Therefore, sending the ideal address to get back the successor of the address, which is the real address for the finger table.
    public String getSuccessor();

    @GET
    @Path("/getPredecessor")
    @Produces({ MediaType.APPLICATION_JSON })
    // For adding a node it is required to create a finger table.
    // Therefore, sending the address to get back the predecessor of the address, which is the real address for the finger table.
    public String getPredecessor();

    @POST
    @Path("/setPredecessor")
    @Produces({ MediaType.APPLICATION_JSON })
    // For adding a node it is required to create a finger table.
    // Therefore, sending the address to get back the predecessor of the address, which is the real address for the finger table.
    public void setPredecessor(@QueryParam("id") int id);

    @POST
    @Path("/update_finger_table")
    @Produces({ MediaType.APPLICATION_JSON })
    // Updating the finger table.
    public void update_finger_table(@QueryParam("s") int s, @QueryParam("i") int i);

    @GET
    @Path("/sendMessage")
    @Produces({ MediaType.APPLICATION_JSON })
    // Sending a message by finding the optimal path.
    public String sendMessage(@QueryParam("key") String key, @QueryParam("message") String message);

    @GET
    @Path("/add")
    @Produces({ MediaType.APPLICATION_JSON })
    // Adding a key value pair by finding the optimal path.
    public String add(@QueryParam("value") String value);

    @GET
    @Path("/get")
    @Produces({ MediaType.APPLICATION_JSON })
    // Getting a value by finding the optimal path.
    public String get(@QueryParam("key") String key);

    @GET
    @Path("/remove")
    @Produces({ MediaType.APPLICATION_JSON })
    // Removing a key value pair by finding the optimal path.
    public String remove(@QueryParam("key") String key);
}
