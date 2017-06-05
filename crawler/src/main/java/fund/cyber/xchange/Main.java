package fund.cyber.xchange;

import fund.cyber.xchange.service.Config;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.IOException;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    
    private static final String CONTEXT_PATH = "/";
    private static final String XML_CONTEXT_NAME = "crawler";
    private static final String MAPPING_URL = "/";
    private static final String WEBAPP_DIRECTORY = "webapp";
    
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    public static void main(String[] args) throws Exception {
        final Config config = new Config();
        new Main().startJetty(config.getPort());
    }

    private void startJetty(int port) throws Exception {
        LOGGER.debug("Starting server at port {}", port);
        Server server = new Server(port);

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{getResourceHandler(), getServletContextHandler()});
        server.setHandler(handlers);

        addRuntimeShutdownHook(server);

        server.start();
        LOGGER.info("Server started at port {}", port);
        server.join();
    }

    private static ResourceHandler getResourceHandler() throws IOException {
        ResourceHandler handler = new ResourceHandler();
        handler.setDirectoriesListed(true);
        handler.setWelcomeFiles(new String[]{ "index.html" });

        handler.setResourceBase(new ClassPathResource(WEBAPP_DIRECTORY).getURI().toString());
        return handler;
    }

    private static ServletContextHandler getServletContextHandler() throws IOException {

        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        contextHandler.setErrorHandler(null);

        contextHandler.setResourceBase(new ClassPathResource(WEBAPP_DIRECTORY).getURI().toString());
        contextHandler.setContextPath(CONTEXT_PATH);

        // Spring
        WebApplicationContext webAppContext = getWebApplicationContext();
        DispatcherServlet dispatcherServlet = new DispatcherServlet(webAppContext);
        ServletHolder springServletHolder = new ServletHolder("mvc-dispatcher", dispatcherServlet);
        contextHandler.addServlet(springServletHolder, MAPPING_URL);
        contextHandler.addEventListener(new ContextLoaderListener(webAppContext));

        return contextHandler;
    }

    private static WebApplicationContext getWebApplicationContext() {
        XmlWebApplicationContext context = new XmlWebApplicationContext();
        context.setNamespace(XML_CONTEXT_NAME);
        return context;
    }
    
    private static void addRuntimeShutdownHook(final Server server) {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                if (server.isStarted()) {
                	server.setStopAtShutdown(true);
                    try {
                    	server.stop();
                    } catch (Exception e) {
                        System.out.println("Error while stopping jetty server: " + e.getMessage());
                        LOGGER.error("Error while stopping jetty server: " + e.getMessage(), e);
                    }
                }
            }
        }));
	}


}
