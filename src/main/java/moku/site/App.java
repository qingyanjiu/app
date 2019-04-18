package moku.site;

import moku.site.context.ContextContainer;
import moku.site.server.HttpServer;
import moku.site.server.HttpServerConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class App
{
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main( String[] args )
    {
        ContextContainer contextContainer = ContextContainer.getInstance();
        int httpServerPort = HttpServerConstant.HTTP_SERVER_PORT;
        Properties properties = new Properties();
        try {
            properties.load(App.class.getClassLoader().getResourceAsStream("config.properties"));
            contextContainer.setProperties(properties);
            if(properties != null && properties.size() > 0 && properties.get("http.server.port") != null)
                httpServerPort = Integer.parseInt(properties.getProperty("http.server.port"));
            HttpServer server = new HttpServer(httpServerPort);
            server.start();
        } catch (IOException e) {
            logger.error("load config file error ["+ e.getMessage() +"]");
        } catch (InterruptedException e) {
            logger.error("start HTTP server failed ["+ e.getMessage() +"]");
        }
    }
}
;