package no.statkart.skif.storetest.wsapi.config;

import com.google.inject.Injector;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.service.module.server.WSServerModule;
import no.statkart.skif.service.module.server.WSServerServiceModule;
import no.statkart.skif.service.proxy.W2DAdapterWithServiceContextSVMapperProxyHandler;
import no.statkart.skif.storetest.config.*;
import no.statkart.skif.storetest.wsapi.StoreTestServiceContextMapper;
import no.statkart.skif.storetest.wsapi.exception.mapping.StoreTestExceptionMapper;
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
        ClassLoader classLoader = getClass().getClassLoader();

        Injector ejbServiceInjector = StoreTestServerInjector.getInjector();
        final ModuleConfiguration configuration = ejbServiceInjector.getInstance(ModuleConfiguration.class);
        final Mapping mapping = ejbServiceInjector.getInstance(StoreTestMapper.class).getMapping();
        injector = ejbServiceInjector.createChildInjector(
                new WSServerModule(configuration, classLoader),

                // Services som ikke har ServiceContext
                new WSServerServiceModule(configuration, new StoreTestGroup1Services().getServices(), mapping, classLoader)
                        .setExceptionMapping(new StoreTestExceptionMapper().getMapping()),
                new WSServerServiceModule(configuration, new StoreTestTxManagementServices().getServices(), mapping, classLoader)
                        .setExceptionMapping(new StoreTestExceptionMapper().getMapping()),

                // Services som har ServiceContext
                new WSServerServiceModule(configuration, new StoreTestTestServices().getServices(), mapping, classLoader)
                        .setExceptionMapping(new StoreTestExceptionMapper().getMapping())
                        .setServiceContextMapperClass(StoreTestServiceContextMapper.class),
                new WSServerServiceModule(configuration, new StoreTestStoreServices().getServices(), mapping, classLoader)
                        .setExceptionMapping(new StoreTestExceptionMapper().getMapping())
                        .setServiceContextMapperClass(StoreTestServiceContextMapper.class, W2DAdapterWithServiceContextSVMapperProxyHandler.class),
                new WSServerServiceModule(configuration, new StoreTestStoreUpdateServices().getServices(), mapping, classLoader)
                        .setExceptionMapping(new StoreTestExceptionMapper().getMapping())
                        .setServiceContextMapperClass(StoreTestServiceContextMapper.class),
                new WSServerServiceModule(configuration, new StoreTestSequenceBlockAllocatorServices().getServices(), mapping, classLoader)
                        .setExceptionMapping(new StoreTestExceptionMapper().getMapping())
                        .setServiceContextMapperClass(StoreTestServiceContextMapper.class),
                new WSServerServiceModule(configuration, new StoreTestDomainFinderServices().getServices(), mapping, classLoader)
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
