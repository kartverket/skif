package no.statkart.skif.storetest.wsapi.config;

import com.google.inject.Injector;
import com.google.inject.servlet.ServletModule;
import no.statkart.skif.mapper.IdentityMapper;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.service.module.server.WSServerModule;
import no.statkart.skif.service.module.server.WSServerServiceModule;
import no.statkart.skif.storetest.config.StoreTestTxManagementServerInjector;
import no.statkart.skif.storetest.config.StoreTestTxManagementServices;
import no.statkart.skif.storetest.wsapi.exception.impl.mapping.StoreTestExceptionMapper;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Konfigurasjon av injector for Web service API. Må kalles fra en ServletContextListener i web.xml.
 *
 * @author Henrik Fredholm
 */
public class StoreTestTxManagementWebServiceInjectorConfig implements ServletContextListener {
    private static volatile Injector injector;

    public static Injector getWebServiceInjector() {
        return injector;
    }


    public void createInjector() {
        final Mapping mapping = new IdentityMapper().getMapping();


        ClassLoader classLoader = getClass().getClassLoader();

        Injector ejbServiceInjector = StoreTestTxManagementServerInjector.getInjector();
        ModuleConfiguration  configuration = ejbServiceInjector.getInstance(ModuleConfiguration.class);

        injector = ejbServiceInjector.createChildInjector(
                new ServletModule(),
                new WSServerModule(configuration, classLoader),
                new WSServerServiceModule(configuration, new StoreTestTxManagementServices().getServices(), mapping, classLoader)
                        .setExceptionMapping(new StoreTestExceptionMapper().getMapping())
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
