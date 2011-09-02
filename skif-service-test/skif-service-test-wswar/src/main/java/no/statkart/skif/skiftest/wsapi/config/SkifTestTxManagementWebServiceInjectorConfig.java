package no.statkart.skif.skiftest.wsapi.config;

import com.google.inject.Injector;
import com.google.inject.servlet.ServletModule;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.service.module.server.WSServerModule;
import no.statkart.skif.service.module.server.WSServerServiceModule;
import no.statkart.skif.skiftest.config.*;
import no.statkart.skif.skiftest.wsapi.exception.impl.mapping.SkifTestExceptionMapper;
import no.statkart.skif.skiftest.wsapi.mapping.SkifTestMapper;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Konfigurasjon av injector for Web service API. Må kalles fra en ServletContextListener i web.xml.
 *
 * @author Henrik Fredholm
 */
public class SkifTestTxManagementWebServiceInjectorConfig implements ServletContextListener {
    private static volatile Injector injector;

    public static Injector getWebServiceInjector() {
        return injector;
    }


    public void createInjector() {
        final Mapping mapping = new SkifTestMapper().getMapping();


        ClassLoader classLoader = getClass().getClassLoader();

        Injector ejbServiceInjector = SkifTestTxManagementServerInjector.getInjector();
        ModuleConfiguration configuration = ejbServiceInjector.getInstance(ModuleConfiguration.class);

        List<Class<?>> services = new ArrayList<Class<?>>(new SkifTestTxManagementServices().getServices());
        services.addAll(new SkifTestSequenceBlockAllocatorServices().getServices());
        injector = ejbServiceInjector.createChildInjector(
                new ServletModule(),
                new WSServerModule(configuration, classLoader),
                new WSServerServiceModule(configuration, services, mapping, classLoader)
                        .setExceptionMapping(new SkifTestExceptionMapper().getMapping())
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
