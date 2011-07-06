package no.statkart.skif.storetest.config;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.SkifModule;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.service.chain.EJBServiceChainFactory;
import no.statkart.skif.service.module.ServerModuleStrategyFactory;
import no.statkart.skif.service.module.server.ServerServiceModule;
import no.statkart.skif.service.module.server.ServerModule;
import no.statkart.skif.service.proxy.ChainedProxyHandler;
import no.statkart.skif.service.proxy.ProxyHandler;
import no.statkart.skif.service.proxy.RuntimeExceptionProxyHandler;
import no.statkart.skif.service.scope.ServiceRequestScoped;
import no.statkart.skif.store.ReplicaVersion;
import no.statkart.skif.store.persistence.hibernate.*;
import no.statkart.skif.storetest.domain.TestBubble;
import org.hibernate.Session;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class StoreTestServerModule extends SkifModule {
    public StoreTestServerModule(Configuration configuration) {
        super(configuration);
    }

    @Override
    protected ModuleStrategyFactory defineDefaultModuleStrategyFactory() {
        return new ServerModuleStrategyFactory();
    }

    @Override
    protected void configure() {
        install(new ServerModule(moduleConfiguration));
        install(new ServerServiceModule(moduleConfiguration, new StoreTestGroup1Services().getServices()));

        // Quick hack for å definere opp Store. Dette skal vere en egen modul:
        bindStore();
    }



    private StoreHibernateSessionFactoryBuilder createHibernateSessionFactoryBuilder() {
        Configuration cfg = new PropertiesConfiguration(getClass().getResource("/no/statkart/skif/storetest/config/persistence/skiftest-hibernate-singlevm.properties"));
        Properties properties = ConfigurationConverter.getProperties(cfg);
        return new StoreHibernateSessionFactoryBuilder(properties, "no/statkart/skif/storetest/persistence/hibernate");
    }

    private void bindStore() {
        StoreHibernateSessionFactoryBuilder sfbuilder = createHibernateSessionFactoryBuilder();
        sfbuilder.addResource(TestBubble.class);

        // Alle requester skal dele samme factory manager, mens session managers kun skal deles per request
        bind(StoreHibernateSessionFactoryBuilder.class).toInstance(sfbuilder);
        bind(StoreHibernateSessionFactoryManager.class).in(Singleton.class);
        bind(StoreHibernateSessionManager.class).in(ServiceRequestScoped.class);
        bind(ReplicaVersion.class).toInstance(ReplicaVersion.CURRENT);
        bind(StoreHibernateSession.class).toProvider(StoreHibernateSessionProvider.class).in(ServiceRequestScoped.class);
        bind(Session.class).toProvider(HibernateSessionProvider.class).in(ServiceRequestScoped.class);

    }
}

