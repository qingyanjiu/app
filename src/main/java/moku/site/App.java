package moku.site;

import moku.site.bean.Task;
import moku.site.context.ContextContainer;
import moku.site.core.RequestHandler;
import moku.site.server.HttpServer;
import moku.site.server.HttpServerConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        ContextContainer contextContainer = ContextContainer.getInstance();
        int httpServerPort = HttpServerConstant.HTTP_SERVER_PORT;
        Properties properties = new Properties();
        try {
            properties.load(App.class.getClassLoader().getResourceAsStream("config.properties"));
            contextContainer.setProperties(properties);
            if (properties != null && properties.size() > 0 && properties.get("http.server.port") != null)
                httpServerPort = Integer.parseInt(properties.getProperty("http.server.port"));

            final RequestHandler requestHandler = RequestHandler.getInstance();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        List<Task> list = requestHandler.getProcessingQueue();
                        if(list.size() > 0) {
                            System.out.println("===============All tasks:"+list.size()+"==================");
                            for (Task task : list) {
                                System.out.print(" Path : " + task.getUrl() +
                                        " | Status : " + task.getStatus() +
                                        " | Cost : " + task.getDuringTime() + " ms" +
                                        " | Response : " + task.getResponse() +
                                        "\n-----------------------------------------\n");
                            }
                            System.out.println("===============================================");
                        }
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

            HttpServer server = new HttpServer(httpServerPort);
            server.start();
        } catch (IOException e) {
            logger.error("load config file error");
        } catch (InterruptedException e) {
            logger.error("start HTTP server failed");
        }
    }
}