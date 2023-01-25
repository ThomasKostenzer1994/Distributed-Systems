package REST.server.computation;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class ComputationApp extends Application {

    final static String SERVER_PATH_PREFIX = "/api";
    private Set<Object> singletons = new HashSet<Object>();
 
    public ComputationApp() {
        singletons.add(new ComputationService());
    }
 
    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
    
    public static void main(String args[]) {
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

        System.out.println("Starting computation service");

        // Get load balancer uri
        String loadBalancerUri = getLoadBalancerUri();
        if (loadBalancerUri == null) {
            System.out.println("No load balancer uri exists in environment variables.");
            return;
        }

        // Create client
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(UriBuilder.fromPath("http://" + loadBalancerUri));
        LoadBalancerServiceInterface proxy = target.proxy(LoadBalancerServiceInterface.class);

        try {
            // Register service at load balancer and get back the port
            int servicePort = Integer.parseInt(proxy.registerAndGetPort());

            Server server = new Server(servicePort);

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

            System.out.println("Server registered: " + servicePort);

            // Start server
			server.start();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    private static String getLoadBalancerUri() {
        Map<String, String> env = System.getenv();
        for (String envName : env.keySet()) {
            if (envName.equals("LOAD_BALANCER_URL")) {
                return env.get(envName);
            }
        }

        return null;
    }
}
