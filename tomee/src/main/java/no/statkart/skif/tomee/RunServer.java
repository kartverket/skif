package no.statkart.skif.tomee;

import org.apache.tomee.embedded.Configuration;
import org.apache.tomee.embedded.Container;

import java.nio.file.Path;
import java.nio.file.Paths;

public class RunServer {
    public static void main(String[] args) {
        Configuration configuration = new Configuration();

        configuration.dir(System.getenv("APP_HOME"));
        configuration.setDeleteBaseOnStartup(false);

        Path serverXml = Paths.get(System.getenv("APP_HOME"), "conf", "server.xml");
        configuration.setServerXml(serverXml.toString());

        try (Container container = new Container(configuration)) {
            container.await();
        }
    }
}
