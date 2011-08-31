package no.statkart.skif.storetest.config;

import com.google.inject.Singleton;
import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.SkifModule;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.module.StrategyTuple;
import no.statkart.skif.service.chain.EJBServiceChainFactoryWithTxSpecification;
import no.statkart.skif.service.ejb.EJBResourceManager;
import no.statkart.skif.service.module.server.ServerServiceModule;
import no.statkart.skif.service.module.server.ServerModule;
import no.statkart.skif.service.module.server.ServerServiceModuleStrategy;
import no.statkart.skif.service.scope.ServiceRequestScoped;
import no.statkart.skif.store.ReplicaVersion;
import no.statkart.skif.store.module.StoreServerModuleStrategyFactory;
import no.statkart.skif.store.module.server.ServerStoreModule;
import no.statkart.skif.store.persistence.hibernate.*;
import no.statkart.skif.storetest.domain.TestBubble;
import no.statkart.skif.storetest.domain.TestMap;
import org.hibernate.Session;

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
        // Konfigurer EJBServiceChain til å bruke en factory som har en ProxyHandler for transaksjonshåndtering
        ModuleStrategyFactory factory = new StoreServerModuleStrategyFactory();
//        StrategyTuple<ServerServiceModuleStrategy> prototype = factory.getPrototype(ServerServiceModule.class);
//        prototype.getStrategy(ServiceMode.SINGLE_VM).setEjbServiceChainFactorySpecification(new EJBServiceChainFactoryWithTxSpecification());
//        prototype.getStrategy(ServiceMode.JEE).setEjbServiceChainFactorySpecification(new EJBServiceChainFactoryWithTxSpecification());

        return factory;
    }

    @Override
    protected void configure() {
        install(new ServerModule(moduleConfiguration));
  /*
        ServerStoreModule serverStoreModule = new ServerStoreModule(moduleConfiguration, "no/statkart/skif/storetest/persistence/hibernate") {
            @Override
            protected void configureHibernate(HibernateStoreSessionFactoryBuilder facotryBuilder) {
                facotryBuilder.addResource(TestBubble.class);
                facotryBuilder.addResource(TestMap.class);
            }
        };
        serverStoreModule.getStrategy(ServiceMode.JEE).setHibernateConfigurationFilename("no/statkart/skif/storetest/config/persistence/skiftest-hibernate-server.properties");
        serverStoreModule.getStrategy(ServiceMode.SINGLE_VM).setHibernateConfigurationFilename("no/statkart/skif/storetest/config/persistence/skiftest-hibernate-singlevm.properties");
        bind(EJBResourceManager.class).to(HibernateStoreEJBResourceManager.class);
        install(serverStoreModule);
   */
        install(new ServerServiceModule(moduleConfiguration, new StoreTestGroup1Services().getServices()));
    }


    private HibernateStoreSessionFactoryBuilder createHibernateSessionFactoryBuilder() {
        Configuration cfg = new PropertiesConfiguration("no/statkart/skif/storetest/config/persistence/skiftest-hibernate-singlevm.properties");
        Properties properties = ConfigurationConverter.getProperties(cfg);
        return new HibernateStoreSessionFactoryBuilder(properties, "no/statkart/skif/storetest/persistence/hibernate");
    }

    private void bindStore() {
        HibernateStoreSessionFactoryBuilder sfbuilder = createHibernateSessionFactoryBuilder();
        sfbuilder.addResource(TestBubble.class);

        // Alle requester skal dele samme factory manager, mens session managers kun skal deles per request
        bind(HibernateStoreSessionFactoryBuilder.class).toInstance(sfbuilder);
        bind(HibernateStoreSessionFactoryManager.class).in(Singleton.class);
        bind(HibernateStoreSessionManager.class).in(ServiceRequestScoped.class);
        bind(ReplicaVersion.class).toInstance(ReplicaVersion.CURRENT);
        bind(HibernateStoreSession.class).toProvider(HibernateStoreSessionProvider.class).in(ServiceRequestScoped.class);
        bind(Session.class).toProvider(HibernateSessionProvider.class).in(ServiceRequestScoped.class);

    }
}

