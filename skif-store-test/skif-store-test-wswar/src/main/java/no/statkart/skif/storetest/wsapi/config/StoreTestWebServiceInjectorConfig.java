package no.statkart.skif.storetest.wsapi.config;

import com.google.inject.Injector;
import com.google.inject.servlet.ServletModule;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.service.module.server.WSServerModule;
import no.statkart.skif.service.module.server.WSServerServiceModule;
import no.statkart.skif.storetest.config.*;
import no.statkart.skif.storetest.wsapi.StoreTestServiceContextMapper;
import no.statkart.skif.storetest.wsapi.exception.impl.mapping.StoreTestExceptionMapper;
import no.statkart.skif.storetest.wsapi.exception.simple.mapping.StoreTestExceptionMapper2;
import no.statkart.skif.storetest.wsapi.mapping.StoreTestMapper;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Konfigurasjon av injector for Web service API. Må kalles fra en ServletContextListener i web.xml.
 *
 * @author Henrik Fredholm
 */
public class StoreTestWebServiceInjectorConfig implements ServletContextListener {
    private static volatile Injector injector;

    public static Injector getWebServiceInjector() {
        return injector;
    }


    public void createInjector() {
        final Mapping mapping = new StoreTestMapper().getMapping();


        ClassLoader classLoader = getClass().getClassLoader();

        Injector ejbServiceInjector = StoreTestServerInjector.getInjector();
        ModuleConfiguration configuration = ejbServiceInjector.getInstance(ModuleConfiguration.class);

        injector = ejbServiceInjector.createChildInjector(
                new ServletModule(),
                new WSServerModule(configuration, classLoader),
                new WSServerServiceModule(configuration, new StoreTestGroup1Services().getServices(), mapping, classLoader).setExceptionMapping(new StoreTestExceptionMapper().getMapping()),
                new WSServerServiceModule(configuration, new StoreTestStoreServices().getServices(), mapping, classLoader)
                        .setExceptionMapping(new StoreTestExceptionMapper().getMapping())
                        .setServiceContextMapperClass(StoreTestServiceContextMapper.class)
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
