package no.statkart.skif.wsversioning.wsapi.v1.config;

import com.google.inject.Injector;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.service.module.server.WSServerModule;
import no.statkart.skif.wsversioning.config.WSVersioningServerV1Injector;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

/**
 * Konfigurasjon av injector for Web service API. Må kalles fra en ServletContextListener i web.xml.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class WSVersioningWebServiceInjectorConfig implements ServletContextListener {
    @EJB
    private WSVersioningServerV1Injector wsVersioningServerV1Injector;

    private static volatile Injector injector;

    public static Injector getWebServiceInjector() {
        return injector;
    }


    public void createInjector() {
        ClassLoader classLoader = getClass().getClassLoader();

        Injector ejbServiceInjector = wsVersioningServerV1Injector.getInjector();
        ModuleConfiguration configuration = ejbServiceInjector.getInstance(ModuleConfiguration.class);

        injector = ejbServiceInjector.createChildInjector(
                new WSServerModule(configuration, classLoader),

                new WSVersioningV1WSServerModule(configuration, classLoader)
        );
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        createInjector();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        injector = null;
    }
}