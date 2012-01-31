package no.statkart.skif.storetest.config;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.SkifModule;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.SkifConfigConstants;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.module.StrategyTuple;
import no.statkart.skif.persistence.ResourceManager;
import no.statkart.skif.persistence.jdbc.ConnectionForSnapshotVersion;
import no.statkart.skif.persistence.jdbc.ConnectionForSnapshotVersionProvider;
import no.statkart.skif.persistence.jdbc.ConnectionManager;
import no.statkart.skif.persistence.jdbc.ConnectionManagerProvider;
import no.statkart.skif.service.chain.EJBServiceChainFactoryWithTxSpecification;
import no.statkart.skif.service.module.ServerModuleStrategyFactory;
import no.statkart.skif.service.module.server.ServerModule;
import no.statkart.skif.service.module.server.ServerServiceModule;
import no.statkart.skif.service.module.server.ServerServiceModuleStrategy;
import no.statkart.skif.service.scope.ServiceRequestScoped;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionSeed;
import no.statkart.skif.store.persistence.hibernate.*;
import no.statkart.skif.store.persistence.jdbc.ConnectionManagerUsingHibernate;
import no.statkart.skif.store.service.ejb.EJBResourceProxyHandlerForHibernate;
import no.statkart.skif.store.persistence.DefaultPersistenceSessionManager;
import no.statkart.skif.store.persistence.PersistenceSessionManager;
import no.statkart.skif.store.persistence.PersistenceSessionManagerProvider;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryBuilder;
import no.statkart.skif.storetest.domain.demo.*;
import org.hibernate.cfg.Environment;

import java.sql.Connection;
import java.util.Properties;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class StoreTestTxManagementServerModule extends SkifModule {
    public StoreTestTxManagementServerModule(ModuleConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected ModuleStrategyFactory defineDefaultModuleStrategyFactory() {
        // Konfigurer EJBServiceChain til å bruke en factory som har en ProxyHandler for transaksjonshåndtering
        ModuleStrategyFactory factory = new ServerModuleStrategyFactory();
        StrategyTuple<ServerServiceModuleStrategy> prototype = factory.getPrototype(ServerServiceModule.class);
        prototype.getStrategy(ServiceMode.SINGLE_VM).setEjbServiceChainFactorySpecification(new EJBServiceChainFactoryWithTxSpecification(EJBResourceProxyHandlerForHibernate.class));
        prototype.getStrategy(ServiceMode.JEE).setEjbServiceChainFactorySpecification(new EJBServiceChainFactoryWithTxSpecification(EJBResourceProxyHandlerForHibernate.class));
        return factory;
    }

    @Override
    protected void configure() {
        install(new ServerModule(moduleConfiguration));

        install(new ServerServiceModule(moduleConfiguration, new StoreTestTxManagementServices().getServices()));
        install(new ServerServiceModule(moduleConfiguration, new StoreTestSequenceBlockAllocatorServices().getServices()));

        bind(ConnectionManager.class).toProvider(ConnectionManagerProvider.class);
        bind(Connection.class).to(ConnectionForSnapshotVersion.class);
        bind(ConnectionForSnapshotVersion.class).toProvider(ConnectionForSnapshotVersionProvider.class);
        bind(PersistenceSessionManager.class).toProvider(PersistenceSessionManagerProvider.class);


    }

    @Provides
    @Singleton
    HibernateSessionFactoryManagerBundle provideHibernateSessionFactoryManagerBundle() {
        Configuration configuration = moduleConfiguration.getConfiguration();

        // TODO: Hent directory fra moduleConfiguration
        HibernateSessionFactoryBuilder hibernateSessionFactoryBuilder = new HibernateSessionFactoryBuilderImpl("no/statkart/skif/storetest/persistence/hibernate")
                .addResource(TestEntity.class)
                .addResource(TestBubble.class)
                .addResource(ChildBubble.class)
                .addResource(ParrentBubble.class)
                .addResource(FilteredBubble.class)
                .addResource(Foo.class);


        Properties hibernatePropertiesCurrent;
        Properties hibernatePropertiesOld;
        if (moduleConfiguration.getServiceMode() == ServiceMode.SINGLE_VM) {
            String username = configuration.getString(SkifConfigConstants.DB_USERNAME);
            String password = configuration.getString(SkifConfigConstants.DB_PASSWORD);
            String sid = configuration.getString(SkifConfigConstants.DB_SID);
            String hostname = configuration.getString(SkifConfigConstants.DB_HOSTNAME);
            String port = configuration.getString(SkifConfigConstants.DB_PORT);
            String url = String.format("jdbc:oracle:thin:@%s:%s:%s", hostname, port, sid);

            hibernatePropertiesCurrent = ConfigurationConverter.getProperties(new PropertiesConfiguration("no/statkart/skif/storetest/config/persistence/skiftest-hibernate-singlevm.properties"));
            hibernatePropertiesOld = hibernatePropertiesCurrent;
            // TODO: Set properties fra konfigurasjon
            //hibernateProperties.setProperty(Environment.USER, username);
            //hibernateProperties.setProperty(Environment.PASS, password);
            //hibernateProperties.setProperty(Environment.URL, url);
        } else {
            String datasourceCurrent = configuration.getString(SkifConfigConstants.DB_DATASOURCE, "no.statkart.matrikkel.persistens.MatrikkelBok_DS");
            hibernatePropertiesCurrent = ConfigurationConverter.getProperties(new PropertiesConfiguration("no/statkart/skif/storetest/config/persistence/skiftest-hibernate-server.properties"));
            hibernatePropertiesCurrent.setProperty(Environment.DATASOURCE, datasourceCurrent);

            String datasourceOld = configuration.getString(SkifConfigConstants.DB_DATASOURCE, "no.statkart.matrikkel.persistens.MatrikkelOld_DS");
            hibernatePropertiesOld = ConfigurationConverter.getProperties(new PropertiesConfiguration("no/statkart/skif/storetest/config/persistence/skiftest-hibernate-server.properties"));
            hibernatePropertiesOld.setProperty(Environment.DATASOURCE, datasourceOld);
        }

        HibernateSessionFactoryManagerBundle hibernateSessionFactoryManagerBundle = new HibernateSessionFactoryManagerBundle(hibernateSessionFactoryBuilder,
                new HibernateSessionFactoryDescriptor("CURRENT(HISTORIC-SCHEMA)", new SnapshotVersionSeed(SnapshotVersion.CURRENT), true, false, hibernatePropertiesCurrent),
                new HibernateSessionFactoryDescriptor("OLD(HISTORIC-SCHEMA)", new SnapshotVersionSeed(SnapshotVersion.OLD), true, true, hibernatePropertiesOld)
        );
        return hibernateSessionFactoryManagerBundle;
    }

    @Provides
    @ServiceRequestScoped
    ResourceManager provideResourceManager(HibernateSessionFactoryManagerBundle hibernateSessionFactoryManagerBundle) {
        Configuration configuration = moduleConfiguration.getConfiguration();
        ConnectionManager connectionManager;
        Properties hibernatePropertiesCurrent;
        Properties hibernatePropertiesOld;
        if (moduleConfiguration.getServiceMode() == ServiceMode.SINGLE_VM) {
            String username = configuration.getString(SkifConfigConstants.DB_USERNAME);
            String password = configuration.getString(SkifConfigConstants.DB_PASSWORD);
            String sid = configuration.getString(SkifConfigConstants.DB_SID);
            String hostname = configuration.getString(SkifConfigConstants.DB_HOSTNAME);
            String port = configuration.getString(SkifConfigConstants.DB_PORT);
            String url = String.format("jdbc:oracle:thin:@%s:%s:%s", hostname, port, sid);

            hibernatePropertiesCurrent = ConfigurationConverter.getProperties(new PropertiesConfiguration("no/statkart/skif/storetest/config/persistence/skiftest-hibernate-singlevm.properties"));
            hibernatePropertiesOld = hibernatePropertiesCurrent;
            // TODO: Set properties fra konfigurasjon
            //hibernateProperties.setProperty(Environment.USER, username);
            //hibernateProperties.setProperty(Environment.PASS, password);
            //hibernateProperties.setProperty(Environment.URL, url);
        } else {
            String datasourceCurrent = configuration.getString(SkifConfigConstants.DB_DATASOURCE, "no.statkart.matrikkel.persistens.MatrikkelBok_DS");
            hibernatePropertiesCurrent = ConfigurationConverter.getProperties(new PropertiesConfiguration("no/statkart/skif/storetest/config/persistence/skiftest-hibernate-server.properties"));
            hibernatePropertiesCurrent.setProperty(Environment.DATASOURCE, datasourceCurrent);

            String datasourceOld = configuration.getString(SkifConfigConstants.DB_DATASOURCE, "no.statkart.matrikkel.persistens.MatrikkelOld_DS");
            hibernatePropertiesOld = ConfigurationConverter.getProperties(new PropertiesConfiguration("no/statkart/skif/storetest/config/persistence/skiftest-hibernate-server.properties"));
            hibernatePropertiesOld.setProperty(Environment.DATASOURCE, datasourceOld);
        }

        PersistenceSessionManager persistenceSessionManager = new DefaultPersistenceSessionManager(
                new HibernatePersistenceSessionMasterImpl(hibernateSessionFactoryManagerBundle.getBundle().get(0)),
                new HibernatePersistenceSessionMasterImpl(hibernateSessionFactoryManagerBundle.getBundle().get(1))
        );
        connectionManager = new ConnectionManagerUsingHibernate(persistenceSessionManager);


        ResourceManager resourceManager = new ResourceManager(
                new ResourceManager.Entry(
                        new ConnectionManagerUsingHibernate(persistenceSessionManager),
                        ConnectionManager.class
                ),
                new ResourceManager.Entry(
                        persistenceSessionManager,
                        PersistenceSessionManager.class
                )
        );
        return resourceManager;
    }
}


