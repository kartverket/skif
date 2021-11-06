package no.statkart.skif.storetest.wsapi.config;

import com.google.inject.Injector;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.service.module.server.WSServerModule;
import no.statkart.skif.service.module.server.WSServerServiceModule;
import no.statkart.skif.service.proxy.W2DAdapterWithServiceContextSVMapperProxyHandler;
import no.statkart.skif.storetest.config.*;
import no.statkart.skif.storetest.wsapi.StoreTestServiceContextMapper;
import no.statkart.skif.storetest.wsapi.exception.mapping.StoreTestExceptionMapper;
import no.statkart.skif.storetest.wsapi.exception.mapping.StoreTestExceptionMapping;
import no.statkart.skif.storetest.wsapi.mapping.StoreTestMapper;
import no.statkart.skif.storetest.wsapi.mapping.StoreTestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Konfigurasjon av injector for Web service API. Må kalles fra en ServletContextListener i web.xml.
 *
 * @author Henrik Fredholm
 */
// Weblogic bruker ServletContextListener som et marker interface for å plukke opp klassen, mens Spring
// bruker @Component. I begge varianter fører til at @PostConstruct alltid blir kjørt først.
@Component
public class StoreTestWebServiceInjectorConfig implements ServletContextListener {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private static volatile Injector injector;

    @EJB
    @Autowired
    private StoreTestServerInjector storeTestServerInjector;

    public static Injector getWebServiceInjector() {
        return injector;
    }

    @PostConstruct
    public void createInjector() {
        try {
            logger.info("Creating Web Service injector");
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
        } catch (Exception e) {
            logger.error("Injector creation failed", e);
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        if (injector == null) {
            throw new ImplementationException("Injector=null, unexpected behavior. Perhaps we need to call 'createInjector()");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        logger.info("Destroying Web Service injector");
        injector = null;
    }
}
