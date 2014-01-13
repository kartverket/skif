package no.statkart.skif.wsversioning.wsapi.v2.config;

import com.google.inject.Injector;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.service.module.server.WSServerModule;
import no.statkart.skif.wsversioning.config.WSVersioningServerInjector;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Konfigurasjon av injector for Web service API. Må kalles fra en ServletContextListener i web.xml.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class WSVersioningWebServiceInjectorConfig implements ServletContextListener {
    private static volatile Injector injector;

    public static Injector getWebServiceInjector() {
        return injector;
    }


    public void createInjector() {
        ClassLoader classLoader = getClass().getClassLoader();

        Injector ejbServiceInjector = WSVersioningServerInjector.getInjector();
        ModuleConfiguration configuration = ejbServiceInjector.getInstance(ModuleConfiguration.class);

        injector = ejbServiceInjector.createChildInjector(
                new WSServerModule(configuration, classLoader),

                new WSVersioningV2WSServerModule(configuration, classLoader)
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