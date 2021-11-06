package no.statkart.skif.skiftest.wsapi.config;

import com.google.inject.Injector;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.service.module.server.WSServerModule;
import no.statkart.skif.service.module.server.WSServerServiceModule;
import no.statkart.skif.skiftest.config.SkifTestGroup1Services;
import no.statkart.skif.skiftest.config.SkifTestGroup2Services;
import no.statkart.skif.skiftest.config.SkifTestGroupABCDServices;
import no.statkart.skif.skiftest.config.SkifTestGroupExServices;
import no.statkart.skif.skiftest.config.SkifTestServerInjector;
import no.statkart.skif.skiftest.wsapi.SkifTestServiceContextMapper;
import no.statkart.skif.skiftest.wsapi.exception.impl.mapping.SkifTestExceptionMapper;
import no.statkart.skif.skiftest.wsapi.exception.simple.mapping.SkifTestSimpleExceptionMapper;
import no.statkart.skif.skiftest.wsapi.mapping.SkifTestMapper;
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
public class SkifTestWebServiceInjectorConfig implements ServletContextListener {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static volatile Injector injector;

    @EJB
    @Autowired
    private SkifTestServerInjector skifTestServerInjector;

    public static Injector getWebServiceInjector() {
        return injector;
    }

    @PostConstruct
    public void createInjector() {
        try {
            logger.info("Creating Web Service injector");
            final Mapping mapping = new SkifTestMapper().getMapping();

            ClassLoader classLoader = getClass().getClassLoader();

            Injector ejbServiceInjector = skifTestServerInjector.getInjector();
            ModuleConfiguration configuration = ejbServiceInjector.getInstance(ModuleConfiguration.class);

            injector = ejbServiceInjector.createChildInjector(
                    new WSServerModule(configuration, classLoader),
                    addLogging(new WSServerServiceModule(configuration, new SkifTestGroup1Services().getServices(), mapping, classLoader).setServiceContextMapperClass(SkifTestServiceContextMapper.class)),
                    addLogging(new WSServerServiceModule(configuration, new SkifTestGroup2Services().getServices(), mapping, classLoader)
                            .setServiceContextMapperClass(SkifTestServiceContextMapper.class)).setExceptionMapping(new SkifTestSimpleExceptionMapper().getMapping()),
                    addLogging(new WSServerServiceModule(configuration, new SkifTestGroupABCDServices().getServices(), mapping, classLoader)
                            .setExceptionMapping(new SkifTestExceptionMapper().getMapping())),
                    addLogging(new WSServerServiceModule(configuration, new SkifTestGroupExServices().getServices(), mapping, classLoader).
                            setExceptionMapping(new SkifTestSimpleExceptionMapper().getMapping()))
            );
        } catch (Exception e) {
            logger.error("Injector creation failed", e);
        }
    }

    private static WSServerServiceModule addLogging(WSServerServiceModule wsServerServiceModule) {
        wsServerServiceModule.getStrategy(ServiceMode.JEE).setWsServiceChainFactoryClassForWSI(WSServiceChainWithLoggingFactory.class);
        return wsServerServiceModule;
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
