package no.statkart.skif.storetest.wsapi.config;

import com.google.inject.Injector;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.service.module.server.WSServerModule;
import no.statkart.skif.service.module.server.WSServerServiceModule;
import no.statkart.skif.service.proxy.W2DAdapterWithServiceContextSVMapperProxyHandler;
import no.statkart.skif.storetest.config.StoreTestDomainFinderServices;
import no.statkart.skif.storetest.config.StoreTestGroup1Services;
import no.statkart.skif.storetest.config.StoreTestSequenceBlockAllocatorServices;
import no.statkart.skif.storetest.config.StoreTestServerInjector;
import no.statkart.skif.storetest.config.StoreTestStoreServices;
import no.statkart.skif.storetest.config.StoreTestStoreUpdateServices;
import no.statkart.skif.storetest.config.StoreTestTestServices;
import no.statkart.skif.storetest.config.StoreTestTxManagementServices;
import no.statkart.skif.storetest.wsapi.StoreTestServiceContextMapper;
import no.statkart.skif.storetest.wsapi.exception.mapping.StoreTestExceptionMapper;
import no.statkart.skif.storetest.wsapi.exception.mapping.StoreTestExceptionMapping;
import no.statkart.skif.storetest.wsapi.mapping.StoreTestMapper;
import no.statkart.skif.storetest.wsapi.mapping.StoreTestMapping;

/**
 * Konfigurasjon av injector for Web service API. Må kalles fra en ServletContextListener i web.xml.
 *
 * @author Henrik Fredholm
 */
public class StoreTestWebServiceInjectorConfig implements ServletContextListener {
    private static volatile Injector injector;

    @EJB
    private StoreTestServerInjector storeTestServerInjector;

    public static Injector getWebServiceInjector() {
        return injector;
    }


    public void createInjector() {
        ClassLoader classLoader = getClass().getClassLoader();

        Injector ejbServiceInjector = storeTestServerInjector.getInjector();
        final ModuleConfiguration configuration = ejbServiceInjector.getInstance(ModuleConfiguration.class);
        final StoreTestMapping mapping = ejbServiceInjector.getInstance(StoreTestMapper.class).getMapping();
        final StoreTestExceptionMapping exceptionMapping = new StoreTestExceptionMapper(mapping).getMapping();
        injector = ejbServiceInjector.createChildInjector(
                new WSServerModule(configuration, classLoader),

                // Services som ikke har ServiceContext
                new WSServerServiceModule(configuration, new StoreTestGroup1Services().getServices(), mapping, classLoader)
                        .setExceptionMapping(exceptionMapping),
                new WSServerServiceModule(configuration, new StoreTestTxManagementServices().getServices(), mapping, classLoader)
                        .setExceptionMapping(exceptionMapping),

                // Services som har ServiceContext
                new WSServerServiceModule(configuration, new StoreTestTestServices().getServices(), mapping, classLoader)
                        .setExceptionMapping(exceptionMapping)
                        .setServiceContextMapperClass(StoreTestServiceContextMapper.class),
                new WSServerServiceModule(configuration, new StoreTestStoreServices().getServices(), mapping, classLoader)
                        .setExceptionMapping(exceptionMapping)
                        .setServiceContextMapperClass(StoreTestServiceContextMapper.class, W2DAdapterWithServiceContextSVMapperProxyHandler.class),
                new WSServerServiceModule(configuration, new StoreTestStoreUpdateServices().getServices(), mapping, classLoader)
                        .setExceptionMapping(exceptionMapping)
                        .setServiceContextMapperClass(StoreTestServiceContextMapper.class),
                new WSServerServiceModule(configuration, new StoreTestSequenceBlockAllocatorServices().getServices(), mapping, classLoader)
                        .setExceptionMapping(exceptionMapping)
                        .setServiceContextMapperClass(StoreTestServiceContextMapper.class),
                new WSServerServiceModule(configuration, new StoreTestDomainFinderServices().getServices(), mapping, classLoader)
                        .setExceptionMapping(exceptionMapping)
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
