package no.statkart.skif.skiftest.wsapi.config;

import com.google.inject.Injector;
import com.google.inject.servlet.ServletModule;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.service.module.server.WSServerModule;
import no.statkart.skif.service.module.server.WSServerServiceModule;
import no.statkart.skif.skiftest.config.*;
import no.statkart.skif.skiftest.wsapi.SkifTestServiceContextMapper;
import no.statkart.skif.skiftest.wsapi.exception.mapping.SkifTestExceptionMapper;
import no.statkart.skif.skiftest.wsapi.exception.mapping2.SkifTestExceptionMapper2;
import no.statkart.skif.skiftest.wsapi.mapping.SkifTestMapper;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Konfigurasjon av injector for Web service API. Må kalles fra en ServletContextListener i web.xml.
 *
 * @author Henrik Fredholm
 */
public class SkifTestWebServiceInjectorConfig implements ServletContextListener {
    private static volatile Injector injector;

    public static Injector getWebServiceInjector() {
        return injector;
    }


    public static void createInjector() {
        final Mapping mapping = new SkifTestMapper().getMapping();

        Injector ejbServiceInjector = SkifTestServerInjector.getInjector();
        ModuleConfiguration  configuration = ejbServiceInjector.getInstance(ModuleConfiguration.class);

        injector = ejbServiceInjector.createChildInjector(
                new ServletModule(),
                new WSServerModule(configuration),
                new WSServerServiceModule(configuration, new SkifTestGroup1Services().getServices(), mapping),
                new WSServerServiceModule(configuration, new SkifTestGroup2Services().getServices(), mapping)
                    .setServiceContextMapperClass(SkifTestServiceContextMapper.class),
                new WSServerServiceModule(configuration, new SkifTestGroupABCDServices().getServices(), mapping)
                        .setExceptionMapping(new SkifTestExceptionMapper().getMapping()),
                new WSServerServiceModule(configuration, new SkifTestGroupExServices().getServices(), mapping).
                        setExceptionMapping(new SkifTestExceptionMapper2().getMapping())

        );
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
