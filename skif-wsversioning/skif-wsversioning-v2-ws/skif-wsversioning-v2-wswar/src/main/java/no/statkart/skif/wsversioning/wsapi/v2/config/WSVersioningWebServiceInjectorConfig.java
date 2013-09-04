package no.statkart.skif.wsversioning.wsapi.v2.config;

import com.google.inject.Injector;
import com.google.inject.servlet.ServletModule;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.service.module.server.WSServerModule;
import no.statkart.skif.service.module.server.WSServerServiceModule;
import no.statkart.skif.wsversioning.config.WSVersioningServerInjector;
import no.statkart.skif.wsversioning.config.WSVersioningServices;
import no.statkart.skif.wsversioning.wsapi.v2.exception.mapping.WSVersioningExceptionMapper;
import no.statkart.skif.wsversioning.wsapi.v2.mapping.WSVersioningMapper;
import no.statkart.skif.wsversioning.wsapi.v2.mapping.WSVersioningServiceContextMapper;

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
        final Mapping mapping = new WSVersioningMapper().getMapping();


        ClassLoader classLoader = getClass().getClassLoader();

        Injector ejbServiceInjector = WSVersioningServerInjector.getInjector();
        ModuleConfiguration configuration = ejbServiceInjector.getInstance(ModuleConfiguration.class);

        injector = ejbServiceInjector.createChildInjector(
                new ServletModule(),
                new WSServerModule(configuration, classLoader),

                new WSServerServiceModule(configuration, new WSVersioningServices().getServices(), mapping, classLoader)
                        .setExceptionMapping(new WSVersioningExceptionMapper().getMapping())
                        .setServiceContextMapperClass(WSVersioningServiceContextMapper.class)
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