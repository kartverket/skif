package no.statkart.skif.skiftest.wsapi.config;

import com.google.inject.Injector;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.service.module.server.WSServerModule;
import no.statkart.skif.service.module.server.WSServerServiceModule;
import no.statkart.skif.skiftest.config.*;
import no.statkart.skif.skiftest.wsapi.SkifTestServiceContextMapper;
import no.statkart.skif.skiftest.wsapi.exception.impl.mapping.SkifTestExceptionMapper;
import no.statkart.skif.skiftest.wsapi.exception.simple.mapping.SkifTestSimpleExceptionMapper;
import no.statkart.skif.skiftest.wsapi.mapping.SkifTestMapper;

import javax.ejb.EJB;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Konfigurasjon av injector for Web service API. Må kalles fra en ServletContextListener i web.xml.
 *
 * @author Henrik Fredholm
 */
public class SkifTestWebServiceInjectorConfig implements ServletContextListener {
    private static volatile Injector injector;

    @EJB
    private SkifTestServerInjector skifTestServerInjector;

    public static Injector getWebServiceInjector() {
        return injector;
    }


    public void createInjector() {
        final Mapping mapping = new SkifTestMapper().getMapping();


        ClassLoader classLoader = getClass().getClassLoader();

        Injector ejbServiceInjector = skifTestServerInjector.getInjector();
        ModuleConfiguration  configuration = ejbServiceInjector.getInstance(ModuleConfiguration.class);

        injector = ejbServiceInjector.createChildInjector(
                new WSServerModule(configuration, classLoader),
                addLogging(new WSServerServiceModule(configuration, new SkifTestGroup1Services().getServices(), mapping,classLoader ).setServiceContextMapperClass(SkifTestServiceContextMapper.class)),
                addLogging(new WSServerServiceModule(configuration, new SkifTestGroup2Services().getServices(), mapping, classLoader)
                    .setServiceContextMapperClass(SkifTestServiceContextMapper.class)),
                addLogging(new WSServerServiceModule(configuration, new SkifTestGroupABCDServices().getServices(), mapping, classLoader)
                        .setExceptionMapping(new SkifTestExceptionMapper().getMapping())),
                addLogging(new WSServerServiceModule(configuration, new SkifTestGroupExServices().getServices(), mapping, classLoader).
                        setExceptionMapping(new SkifTestSimpleExceptionMapper().getMapping()))

        );
    }

    private static WSServerServiceModule addLogging(WSServerServiceModule wsServerServiceModule) {
        wsServerServiceModule.getStrategy(ServiceMode.JEE).setWsServiceChainFactoryClassForWSI(WSServiceChainWithLoggingFactory.class);
        return wsServerServiceModule;
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        createInjector();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        injector=null;
    }
}
