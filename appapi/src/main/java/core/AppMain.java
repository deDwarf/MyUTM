package core;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

// TODO Decide on separate DB module necessity. It is necessary if its going to be imported from
//  admin panel server as well. Otherwise - NO

// TODO If DB module is necessary, find how to import it along with all its dependencies.
//  Currently, all those are imported in root POM, however need to keep them in DB module POM only

// TODO Find a way to deploy this service so it is 24/7 accessible for client dev

// TODO How to store MySQL scripts? Both DDL and DML ones. Should I keep DML ones as files or in code?
//  Where to store DDL? maybe keep them "gitignored" in Database module?

// TODO Test current AUTH methods + introduce refresh tokens.
//  Decide on whether it is that necessary to keep blacklist for logouts

public class AppMain {
    // Base URI the Grizzly HTTP server will listen on
    private static final String BASE_URI_TEMPLATE = "http://%s:%s/";
    private static Logger log = LoggerFactory.getLogger("Main");

    public static void main(String[] args) {
        Properties prop = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (InputStream propFileStream = loader.getResourceAsStream("api.properties")){
            prop.load(propFileStream);
            String host = prop.getProperty("host");
            String port = prop.getProperty("port");
            Context.getInstance().loadDatabaseController("db.properties");
            final HttpServer server = startServer(host, port);

            log.info("Hit enter to exit..");
            int input;
            do {
                input = System.in.read();
            } while (input != 13);
            server.shutdown(10, TimeUnit.SECONDS);
        } catch (FileNotFoundException e) {
            log.error("Properties file does not exist: cannot resolve target hostname & port", e);
        } catch (IOException e) {
            log.error("Failed to read properties file: cannot resolve target hostname & port", e);
        }
    }

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    private static HttpServer startServer(String host, String port) {
        // create a resource config that scans for JAX-RS resources and providers
        String uri = String.format(BASE_URI_TEMPLATE, host, port);
        final ResourceConfig rc = new ResourceConfig(GenericExceptionHandler.class).packages("api");
        log.info("Application started with API reference available at {}application.wadl", uri);

        return GrizzlyHttpServerFactory.createHttpServer(URI.create(uri), rc);
    }
}
