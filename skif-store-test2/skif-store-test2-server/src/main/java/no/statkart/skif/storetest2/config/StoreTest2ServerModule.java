package no.statkart.skif.storetest2.config;

import com.google.common.collect.ImmutableList;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.SkifModule;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.config.SkifConfigConstants;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.module.StrategyTuple;
import no.statkart.skif.persistence.DefaultResourceManager;
import no.statkart.skif.persistence.ResourceManager;
import no.statkart.skif.persistence.ResourceManagerConfigurator;
import no.statkart.skif.persistence.VersionFinder;
import no.statkart.skif.persistence.jdbc.*;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.service.chain.EJBServiceChainFactorySpecification;
import no.statkart.skif.service.ejb.EJBResourceProxyHandlerForConnection;
import no.statkart.skif.service.locker.DBLockerInTransactionService;
import no.statkart.skif.service.locker.DBLockerService;
import no.statkart.skif.service.module.server.RunOnServerServiceModule;
import no.statkart.skif.service.module.server.ServerModule;
import no.statkart.skif.service.module.server.ServerServiceModule;
import no.statkart.skif.service.module.server.ServerServiceModuleStrategy;
import no.statkart.skif.service.scope.ServiceRequestScoped;
import no.statkart.skif.service.sequence.HighLowGenerator;
import no.statkart.skif.service.sequence.IdService;
import no.statkart.skif.service.sequence.IdServiceImpl;
import no.statkart.skif.service.sequence.SequenceBlockAllocatorService;
import no.statkart.skif.service.test.TestdataService;
import no.statkart.skif.store.*;
import no.statkart.skif.store.kodeliste.Kodeliste;
import no.statkart.skif.store.module.StoreServerModuleStrategyFactory;
import no.statkart.skif.store.persistence.*;
import no.statkart.skif.store.persistence.hibernate.*;
import no.statkart.skif.store.persistence.hibernate.type.EnumKodeIdType;
import no.statkart.skif.store.persistence.jdbc.ConnectionManagerUsingHibernate;
import no.statkart.skif.store.persistence.kodeliste.DefaultKodelistePersistenceSessionSubtypeHandler;
import no.statkart.skif.store.persistence.kodeliste.EnumKodelisteManager;
import no.statkart.skif.store.service.StoreService;
import no.statkart.skif.store.service.ejb.EJBResourceProxyHandlerForHibernateWithLocks;
import no.statkart.skif.storetest2.domain.eierskap.Eiendom;
import no.statkart.skif.storetest2.domain.eierskap.Eier;
import no.statkart.skif.storetest2.endringslogg.EndringManager;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.cfg.Environment;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * @author Henrik Fredholm
 * @since 2.2.0
 */
public class StoreTest2ServerModule extends SkifModule {
    public StoreTest2ServerModule(Configuration configuration) {
        super(configuration);
    }

    @Override
    protected ModuleStrategyFactory defineDefaultModuleStrategyFactory() {
        // Konfigurer EJBServiceChain til å bruke en factory som har en ProxyHandler for transaksjonshåndtering
        ModuleStrategyFactory factory = new StoreServerModuleStrategyFactory();
        StrategyTuple<ServerServiceModuleStrategy> prototype = factory.getPrototype(ServerServiceModule.class);
        prototype.getStrategy(ServiceMode.SINGLE_VM).setEjbServiceChainFactorySpecification(new EJBServiceChainFactorySpecification(EJBResourceProxyHandlerForHibernateWithLocks.class));
        prototype.getStrategy(ServiceMode.JEE).setEjbServiceChainFactorySpecification(new EJBServiceChainFactorySpecification(EJBResourceProxyHandlerForHibernateWithLocks.class));

        return factory;
    }

    @Override
    protected void configure() {
        install(new ServerModule(moduleConfiguration));
        install(new RunOnServerServiceModule(moduleConfiguration));

        bind(Store.class).to(StoreServer.class);
        bind(IdService.class).to(IdServiceImpl.class);
        bind(SequenceBlockAllocatorService.class).to(no.statkart.skif.storetest2.service.id.SequenceBlockAllocatorService.class);

        install(new ServerServiceModule(moduleConfiguration, new StoreTest2StoreServices().getServices()));
        install(new ServerServiceModule(moduleConfiguration, new StoreTest2TestServices().getServices()));
        bind(TestdataService.class).to(no.statkart.skif.storetest2.service.test.TestdataService.class);

        {
            // Definer services som ikke bruker Store, men bare SQL connection
            final List<Class<?>> servicesThatOnlyUseConnection = new ArrayList<Class<?>>();
            servicesThatOnlyUseConnection.addAll(new StoreTest2LocalServices().getServices());
            servicesThatOnlyUseConnection.addAll(new StoreTest2SequenceBlockAllocatorServices().getServices());
            final ServerServiceModule moduleThatOnlyUseConnection = new ServerServiceModule(moduleConfiguration, servicesThatOnlyUseConnection);
            moduleThatOnlyUseConnection.getStrategy(ServiceMode.SINGLE_VM).setEjbServiceChainFactorySpecification(new EJBServiceChainFactorySpecification(EJBResourceProxyHandlerForConnection.class));
            moduleThatOnlyUseConnection.getStrategy(ServiceMode.JEE).setEjbServiceChainFactorySpecification(new EJBServiceChainFactorySpecification(EJBResourceProxyHandlerForConnection.class));
            install(moduleThatOnlyUseConnection);
        }

        bind(ConnectionManager.class).toProvider(ConnectionManagerProvider.class);
        bind(Connection.class).to(ConnectionForSnapshotVersion.class);
        bind(PersistenceSessionManager.class).toProvider(PersistenceSessionManagerProvider.class);
        bind(ConnectionForSnapshotVersion.class).toProvider(ConnectionForSnapshotVersionProvider.class);

        bind(SkifUtil.typeLiteral(DBLockerService.class, Long.class)).to(no.statkart.skif.storetest2.service.locker.DBLockerService.class);
        bind(SkifUtil.typeLiteral(DBLockerInTransactionService.class, Long.class)).to(no.statkart.skif.storetest2.service.locker.DBLockerInTransactionService.class);
        bind(LockerStrategy.class).to(TransactionalLockerStrategy.class);
        bind(TransactionalLockerStrategy.class).in(ServiceRequestScoped.class);

        bind(StoreService.class).to(no.statkart.skif.storetest2.service.store.StoreService.class);
        bind(Session.class).toProvider(SessionProvider.class);
        bind(PersistenceSessionForSnapshot.class).toProvider(PersistenceSessionForSnapshotProvider.class);
    }

    @Provides
    @ServiceRequestScoped
    StoreServer provideStoreServer(PersistenceSessionManager persistenceSessionManager, Injector injector, BubbleDependencyComparator bubbleDependencyComparator, LockerStrategy lockerStrategy) {
        //ReadListener
        List<StoreSessionReadListener> readListeners = ImmutableList.of();
        List<StoreSessionWriteListener> writeListeners = ImmutableList.of();
        List<StoreSessionFinishListener> finishListeners = ImmutableList.<StoreSessionFinishListener>of(injector.getInstance(EndringManager.class));
        StoreServer storeServer = new StoreServer(new StoreSessionServer(persistenceSessionManager, injector.getProvider(VersionFinder.class), lockerStrategy, bubbleDependencyComparator, readListeners, writeListeners, finishListeners), injector);
        return storeServer;
    }

    @Provides
    @Singleton
    EnumKodelisteManager provideEnumKodelisteManager() {
        EnumKodelisteManager enumKodelisteManager = new EnumKodelisteManager();
//        enumKodelisteManager.installStatic(.class);
        return enumKodelisteManager;
    }

    /**
     * Angir hvilke kodeliste typer som finnes. Det er egentlig litt unødvendig å måtte angi det her siden
     * den informasjon kan utledes fra hiberante factory.
     *
     * @return
     */
    @Provides
    @Singleton
    Collection<Class<? extends Kodeliste>> provideKodelisteClasses() {
        Collection<Class<? extends Kodeliste>> kodelisteClasses = new ArrayList<Class<? extends Kodeliste>>();
//        kodelisteClasses.add(StoreTest2KodelisteLong.class);
        return kodelisteClasses;
    }

    @Provides
    HibernateInterceptorFactory provideHibernateInterceptorFactory() {
        return new HibernateInterceptorFactory() {
            @Override
            public Interceptor create(SnapshotVersionSeed snapshotVersionSeed) {
                return new HibernateStoreInterceptor(snapshotVersionSeed);
            }
        };
    }


    @Provides
    @Singleton
    HibernateSessionFactoryManagerBundle provideHibernateSessionFactoryManagerBundle(Provider<HibernateInterceptorFactory> hibernateInterceptorFactoryProvider, Provider<IdService> idServiceProvider) {

        Configuration configuration = moduleConfiguration.getConfiguration();

        // TODO: Hent directory fra moduleConfiguration
        HibernateSessionFactoryBuilder hibernateSessionFactoryBuilder = new HibernateSessionFactoryBuilderImpl("no/statkart/skif/storetest2/persistence/hibernate")
                // NB: Rekkefølgen er viktig. Objekter som ikke avhenger av andre må stå først
                .addResource(EnumKodeIdType.class)
                .addResource(Eiendom.class)
                .addResource(Eier.class)
                ;


        PropertiesConfiguration hibernatePropertiesConfiguration = new PropertiesConfiguration("no/statkart/skif/storetest/config/persistence/skiftest-hibernate.properties");

        Properties hibernatePropertiesCurrent;
        Properties hibernatePropertiesOld;
        if (moduleConfiguration.getServiceMode() == ServiceMode.SINGLE_VM) {
            hibernatePropertiesConfiguration.setProperty(Environment.TRANSACTION_STRATEGY, "org.hibernate.transaction.JDBCTransactionFactory");

            String username = configuration.getString(SkifConfigConstants.DB_USERNAME);
            String password = configuration.getString(SkifConfigConstants.DB_PASSWORD);
            String sid = configuration.getString(SkifConfigConstants.DB_SID);
            String hostname = configuration.getString(SkifConfigConstants.DB_HOSTNAME);
            String port = configuration.getString(SkifConfigConstants.DB_PORT);
            String url = String.format("jdbc:oracle:thin:@%s:%s:%s", hostname, port, sid);

            hibernatePropertiesCurrent = ConfigurationConverter.getProperties(hibernatePropertiesConfiguration);

            hibernatePropertiesCurrent.setProperty(Environment.USER, username);
            hibernatePropertiesCurrent.setProperty(Environment.PASS, password);
            hibernatePropertiesCurrent.setProperty(Environment.URL, url);

            hibernatePropertiesOld = hibernatePropertiesCurrent;
        } else {
            hibernatePropertiesConfiguration.setProperty(Environment.TRANSACTION_STRATEGY, "org.hibernate.transaction.JTATransactionFactory");

            String datasourceCurrent = configuration.getString(SkifConfigConstants.DB_DATASOURCE);
            hibernatePropertiesCurrent = ConfigurationConverter.getProperties(hibernatePropertiesConfiguration);
            hibernatePropertiesCurrent.setProperty(Environment.DATASOURCE, datasourceCurrent);

            String datasourceOld = configuration.getString(SkifConfigConstants.DB_DATASOURCE);
            hibernatePropertiesOld = ConfigurationConverter.getProperties(hibernatePropertiesConfiguration);
            hibernatePropertiesOld.setProperty(Environment.DATASOURCE, datasourceOld);
        }

        final HibernateStoreInterceptorFactory hibernateInterceptorFactory = new HibernateStoreInterceptorFactory();
        HibernateSessionFactoryManagerBundle hibernateSessionFactoryManagerBundle = new HibernateSessionFactoryManagerBundle(hibernateSessionFactoryBuilder,
                new HibernateSessionFactoryDescriptor("CURRENT(HISTORIC-SCHEMA)", new SnapshotVersionSeed(SnapshotVersion.CURRENT), true, false, hibernatePropertiesCurrent, hibernateInterceptorFactory),
                new HibernateSessionFactoryDescriptor("OLD(HISTORIC-SCHEMA)", new SnapshotVersionSeed(SnapshotVersion.OLD), true, true, hibernatePropertiesOld, hibernateInterceptorFactory)
        );

        HighLowGenerator.setIdServiceProvider(idServiceProvider);
        return hibernateSessionFactoryManagerBundle;

    }

    @Provides
    @ServiceRequestScoped
    ResourceManager provideResourceManager(Provider<ResourceManagerConfigurator> resourceManagerConfiguratorProvider, Provider<HibernateSessionFactoryManagerBundle> hibernateSessionFactoryManagerBundleProvider, Provider<EnumKodelisteManager> enumKodelisteManagerProvider, Provider<Collection<Class<? extends Kodeliste>>> kodelisteClassesProvider, ServiceContext serviceContext) {
        final String strategy = resourceManagerConfiguratorProvider.get().getStrategy();
        if (strategy == ResourceManagerConfigurator.CONNECTION_ONLY) {
            return createResourceManagerForConnectionOnlyStrategy();
        } else {
            return createResourceManagerForHibernateStrategy(
                    hibernateSessionFactoryManagerBundleProvider.get(),
                    enumKodelisteManagerProvider.get(),
                    kodelisteClassesProvider.get(),
                    serviceContext
            );
        }

    }

    ResourceManager createResourceManagerForConnectionOnlyStrategy() {
        Configuration configuration = moduleConfiguration.getConfiguration();
        ConnectionManager connectionManager;
        if (moduleConfiguration.getServiceMode() == ServiceMode.SINGLE_VM) {
            String username = configuration.getString(SkifConfigConstants.DB_USERNAME);
            String password = configuration.getString(SkifConfigConstants.DB_PASSWORD);
            String sid = configuration.getString(SkifConfigConstants.DB_SID);
            String hostname = configuration.getString(SkifConfigConstants.DB_HOSTNAME);
            String port = configuration.getString(SkifConfigConstants.DB_PORT);
            String url = String.format("jdbc:oracle:thin:@%s:%s:%s", hostname, port, sid);

            connectionManager = new ConnectionManagerUsingFactory(
                    new ConnectionFactoryUsingJDBC(url, username, password, false, SnapshotVersion.CURRENT, false)
            );
        } else {
            String datasource = configuration.getString(SkifConfigConstants.DB_DATASOURCE, "no.statkart.matrikkel.persistens.MatrikkelBok_DS");
            connectionManager = new ConnectionManagerUsingFactory(
                    new ConnectionFactoryUsingDataSource(datasource, false, SnapshotVersion.CURRENT, false)
            );
        }

        ResourceManager resourceManager = new DefaultResourceManager(
                new ResourceManager.Entry(connectionManager, ConnectionManager.class)
        );
        return resourceManager;
    }

    ResourceManager createResourceManagerForHibernateStrategy(HibernateSessionFactoryManagerBundle hibernateSessionFactoryManagerBundle, EnumKodelisteManager enumKodelisteManager, Collection<Class<? extends Kodeliste>> kodelisteClasses, ServiceContext serviceContext) {
        HibernatePersistenceSessionMasterImpl persistenceSessionMasterCurrent = new DefaultHibernatePersistenceSessionImplExt(
                hibernateSessionFactoryManagerBundle.getBundle().get(0)
        );

        HibernatePersistenceSessionMasterImpl persistenceSessionMasterOld = new DefaultHibernatePersistenceSessionImplExt(
                hibernateSessionFactoryManagerBundle.getBundle().get(1)
        );

        PersistenceSessionManager persistenceSessionManager = new DefaultPersistenceSessionManager(
                new DefaultPersistenceSessionStrategy(
                        persistenceSessionMasterCurrent,
                        new DefaultKodelistePersistenceSessionSubtypeHandler(persistenceSessionMasterCurrent, enumKodelisteManager, kodelisteClasses, serviceContext)
                ),
                new DefaultPersistenceSessionStrategy(
                        persistenceSessionMasterOld,
                        new DefaultKodelistePersistenceSessionSubtypeHandler(persistenceSessionMasterOld, enumKodelisteManager, kodelisteClasses, serviceContext)
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

