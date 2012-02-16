package no.statkart.skif.storetest.config;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.util.Providers;
import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.SkifModule;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.config.SkifConfigConstants;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.module.StrategyTuple;
import no.statkart.skif.persistence.DefaultResourceManager;
import no.statkart.skif.persistence.ResourceManager;
import no.statkart.skif.persistence.VersionFinder;
import no.statkart.skif.persistence.jdbc.ConnectionForSnapshotVersion;
import no.statkart.skif.persistence.jdbc.ConnectionForSnapshotVersionProvider;
import no.statkart.skif.persistence.jdbc.ConnectionManager;
import no.statkart.skif.persistence.jdbc.ConnectionManagerProvider;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.service.chain.EJBServiceChainFactoryWithTxSpecification;
import no.statkart.skif.service.locker.DBLockerInTransactionService;
import no.statkart.skif.service.locker.DBLockerService;
import no.statkart.skif.service.module.server.RunOnServerServiceModule;
import no.statkart.skif.service.module.server.ServerModule;
import no.statkart.skif.service.module.server.ServerServiceModule;
import no.statkart.skif.service.module.server.ServerServiceModuleStrategy;
import no.statkart.skif.service.scope.ServiceRequestScoped;
import no.statkart.skif.store.*;
import no.statkart.skif.store.module.StoreServerModuleStrategyFactory;
import no.statkart.skif.store.persistence.*;
import no.statkart.skif.store.persistence.hibernate.*;
import no.statkart.skif.store.persistence.hibernate.type.EnumKodeIdType;
import no.statkart.skif.store.persistence.jdbc.ConnectionManagerUsingHibernate;
import no.statkart.skif.store.persistence.kodeliste.DefaultKodelistePersistenceSessionSubtypeHandler;
import no.statkart.skif.store.persistence.kodeliste.EnumKodelisteManager;
import no.statkart.skif.store.service.ejb.EJBResourceProxyHandlerForHibernateWithLocks;
import no.statkart.skif.storetest.domain.demo.*;
import no.statkart.skif.storetest.domain.demo.koder.*;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLong;
import no.statkart.skif.storetest.filter.AggregertObjektFilter;
import no.statkart.skif.storetest.filter.TestBubbleFilter;
import no.statkart.skif.storetest.filter.TestBubbleFinishFilter;
import no.statkart.skif.storetest.util.DemoKodeMsg;
import no.statkart.skif.util.KodeMsg;
import org.hibernate.Session;
import org.hibernate.cfg.Environment;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
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
        StrategyTuple<ServerServiceModuleStrategy> prototype = factory.getPrototype(ServerServiceModule.class);
        prototype.getStrategy(ServiceMode.SINGLE_VM).setEjbServiceChainFactorySpecification(new EJBServiceChainFactoryWithTxSpecification(EJBResourceProxyHandlerForHibernateWithLocks.class));
        prototype.getStrategy(ServiceMode.JEE).setEjbServiceChainFactorySpecification(new EJBServiceChainFactoryWithTxSpecification(EJBResourceProxyHandlerForHibernateWithLocks.class));

        return factory;
    }

    @Override
    protected void configure() {
        install(new ServerModule(moduleConfiguration));
        install(new RunOnServerServiceModule(moduleConfiguration));

        bind(Store.class).to(StoreServer.class);

        // EnumKode internasjonalisering
        bind(KodeMsg.class).to(DemoKodeMsg.class);

        install(new ServerServiceModule(moduleConfiguration, new StoreTestTxManagementServices().getServices()));
        install(new ServerServiceModule(moduleConfiguration, new StoreTestGroup1Services().getServices()));
        install(new ServerServiceModule(moduleConfiguration, new StoreTestStoreServices().getServices()));
        install(new ServerServiceModule(moduleConfiguration, new StoreTestStoreUpdateServices().getServices()));
        install(new ServerServiceModule(moduleConfiguration, new StoreTestLocalServices().getServices()));
        install(new ServerServiceModule(moduleConfiguration, new StoreTestSequenceBlockAllocatorServices().getServices()));
        install(new ServerServiceModule(moduleConfiguration, new StoreTestTestServices().getServices()));

        bind(ConnectionManager.class).toProvider(ConnectionManagerProvider.class);
        bind(Connection.class).to(ConnectionForSnapshotVersion.class);
        bind(PersistenceSessionManager.class).toProvider(PersistenceSessionManagerProvider.class);
        bind(ConnectionForSnapshotVersion.class).toProvider(ConnectionForSnapshotVersionProvider.class);

        bind(DBLockerService.class).to(no.statkart.skif.storetest.service.locker.DBLockerService.class);
        bind(DBLockerInTransactionService.class).to(no.statkart.skif.storetest.service.locker.DBLockerInTransactionService.class);
        bind(LockerStrategy.class).to(TransactionalLockerStrategy.class);
        bind(TransactionalLockerStrategy.class).in(ServiceRequestScoped.class);

        bind(StoreService.class).to(no.statkart.skif.storetest.service.store.StoreService.class);
        bind(Session.class).toProvider(SessionProvider.class);
        bind(PersistenceSessionForSnapshot.class).toProvider(PersistenceSessionForSnapshotProvider.class);


    }

    @Provides
    @ServiceRequestScoped
    StoreServer provideStoreServer(PersistenceSessionManager persistenceSessionManager) {
        //ReadListener
        List<StoreSessionReadListener> readListeners = new ArrayList<StoreSessionReadListener>();
        readListeners.add(new TestBubbleFilter());
        List<StoreSessionWriteListener> writeListeners = new ArrayList<StoreSessionWriteListener>();
        writeListeners.add(new TestBubbleFilter());
        writeListeners.add(new AggregertObjektFilter());
        List<StoreSessionFinishListener> finishListeners = new ArrayList<StoreSessionFinishListener>();
        finishListeners.add(new TestBubbleFinishFilter());
        StoreServer storeServer = new StoreServer(new StoreSessionServer(persistenceSessionManager, Providers.<VersionFinder>of(null), MemoryLockerSingleton5.getInstance(), readListeners, writeListeners, finishListeners));        
        return storeServer;
    }

    @Provides
    @Singleton
    EnumKodelisteManager provideEnumKodelisteManager() {
        EnumKodelisteManager enumKodelisteManager = new EnumKodelisteManager();
        enumKodelisteManager.installStatic(AEnumKodeId.class);
        enumKodelisteManager.installStatic(BEnumKodeId.class);
        enumKodelisteManager.installStatic(SEnumKodeId.class);
        return enumKodelisteManager;
    }


    @Provides
    @Singleton
    HibernateSessionFactoryManagerBundle provideHibernateSessionFactoryManagerBundle() {

        Configuration configuration = moduleConfiguration.getConfiguration();

        // TODO: Hent directory fra moduleConfiguration
        HibernateSessionFactoryBuilder hibernateSessionFactoryBuilder = new HibernateSessionFactoryBuilderImpl("no/statkart/skif/storetest/persistence/hibernate")
                // NB: Rekkefølgen er viktig. Objekter som ikke avhenger av andre må stå først
                .addResource(EnumKodeIdType.class)
                .addResource(ADbKode.class)
                .addResource(BDbKode.class)
                .addResourceWithSubclasses(CDbKode.class, C1DbKode.class, C2DbKode.class)
                .addResource(XStrDbKode.class)
                .addResource(StoreTestKodelisteLong.class)
                .addResource(TestBubble.class)
                .addResource(ChildBubble.class)
                .addResource(ParrentBubble.class)
                .addResource(FilteredBubble.class)
                .addResource(Foo.class)
                .addResource(Baz.class)
                .addResource(Raz.class)
                .addResource(Bar.class)
                .addResource(BarFoos.class)
                .addResource(TestMap.class)
                .addResource(TestEntity.class)
                .addResource(AggregertObjekt.class);


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
    ResourceManager provideResourceManager(HibernateSessionFactoryManagerBundle hibernateSessionFactoryManagerBundle, EnumKodelisteManager enumKodelisteManager, KodeMsg kodeMsg, ServiceContext serviceContext) {
        Configuration configuration = moduleConfiguration.getConfiguration();
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

        HibernatePersistenceSessionMasterImpl persistenceSessionMasterCurrent = new DefaultHibernatePersistenceSessionImpl326(
                hibernateSessionFactoryManagerBundle.getBundle().get(0)
        );

        HibernatePersistenceSessionMasterImpl persistenceSessionMasterOld = new DefaultHibernatePersistenceSessionImpl326(
                hibernateSessionFactoryManagerBundle.getBundle().get(1)
        );

        PersistenceSessionManager persistenceSessionManager = new DefaultPersistenceSessionManager(
                new DefaultPersistenceSessionStrategy(
                        persistenceSessionMasterCurrent,
                        new DefaultKodelistePersistenceSessionSubtypeHandler(persistenceSessionMasterCurrent, enumKodelisteManager, serviceContext)
                ),
                new DefaultPersistenceSessionStrategy(
                        persistenceSessionMasterOld,
                        new DefaultKodelistePersistenceSessionSubtypeHandler(persistenceSessionMasterOld, enumKodelisteManager, serviceContext)
                )
        );

        ResourceManager resourceManager = new DefaultResourceManager(
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

