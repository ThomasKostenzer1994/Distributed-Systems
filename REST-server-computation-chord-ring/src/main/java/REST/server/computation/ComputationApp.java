package REST.server.computation;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import javax.ws.rs.core.Application;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// Assignment for Michael Karbasch, Stefan Dobler and Thomas Kostenzer

public class ComputationApp extends Application {

    //final static int SERVER_PORT = 8989;
    static final String SERVER_PATH_PREFIX = "/api";
    private static Set<Object> singletons = new HashSet<>();
 
    public ComputationApp() {
    }
 
    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
    
    public static void main(String args[]) {
        // Get chord address.
        String chordAddress = getEnvironmentVariable("CHORD_ADDRESS");
        if (chordAddress == null) {
            System.err.println("No chord address found.");
            return;
        }

        int chordAddressInt;
        try {
            chordAddressInt = Integer.parseInt(chordAddress);
        }
        catch (Exception e) {
            System.err.println("Chord address not parsable." + e.toString());
            return;
        }

        // Create log file
       try {
            File logfile = new File("/var/log/computation-service.log");
            FileOutputStream fileOutputStream = new FileOutputStream(logfile);
            PrintStream printStream = new PrintStream(fileOutputStream);
            System.setOut(printStream);
            System.setErr(printStream);
        }
        catch (Exception e) {
            System.err.println("Not able to create log file");
            e.printStackTrace();
        }

        // Get known node address.
        System.out.println("Get CHORD_NODE");
        String knownNode = getEnvironmentVariable("CHORD_NODE");
        boolean firstNode = false;
        int chordNodeInt = 0;
        if (knownNode == null) {
            firstNode = true;
        }
        else {

            try {
                chordNodeInt = Integer.parseInt(knownNode);
            }
            catch (Exception e) {
                System.err.println("Chord node not parsable." + e);
                return;
            }
        }

        // Create singleton of the computation service
        String additionalText = firstNode ? " and is the first node" : " and uses node " + chordNodeInt;
        System.out.println("Starting computation service of node " + chordAddressInt + additionalText);

        try {
            //singletons.add(new ComputationService(chordAddressInt, firstNode, chordNodeInt, 5, SERVER_PATH_PREFIX, SERVER_PORT));

            Server server = new Server(8989);

            // Setup the basic Application "context" at "/".
            // This is also known as the handler tree (in Jetty speak).
            final ServletContextHandler context = new ServletContextHandler(server, "/");

            // Setup RESTEasy's HttpServletDispatcher at "/api/*".
            final ServletHolder restEasyServlet = new ServletHolder(new HttpServletDispatcher());
            restEasyServlet.setInitParameter("resteasy.servlet.mapping.prefix", SERVER_PATH_PREFIX);
            restEasyServlet.setInitParameter("javax.ws.rs.Application", ComputationApp.class.getCanonicalName());
            context.addServlet(restEasyServlet,  SERVER_PATH_PREFIX + "/*");

            // Setup the DefaultServlet at "/".
            final ServletHolder defaultServlet = new ServletHolder(new DefaultServlet());
            context.addServlet(defaultServlet, "/");

            server.setHandler(context);

            System.out.println("Server registered: " + (8081 + chordAddressInt) + " for node " + chordAddressInt);

            // Start server
			server.start();

            singletons.add(new ComputationService(chordAddressInt, firstNode, chordNodeInt, 5, SERVER_PATH_PREFIX, 8081 + chordAddressInt));


            server.join();
		} catch (Exception e) {
            System.out.println("Server not registered: " + (8081 + chordAddressInt) + " for node " + chordAddressInt + e);
			e.printStackTrace();
		}
    }

    private static String getEnvironmentVariable(String name) {
        Map<String, String> env = System.getenv();
        for (String envName : env.keySet()) {
            if (envName.equals(name)) {
                return env.get(envName);
            }
        }

        return null;
    }
}
