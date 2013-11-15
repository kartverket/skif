package no.statkart.skif.storetest.config;

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
import no.statkart.skif.service.chain.EJBServiceChainFactorySpecification;
import no.statkart.skif.service.ejb.EJBResourceProxyHandlerForConnection;
import no.statkart.skif.service.locker.DBLockerInTransactionService;
import no.statkart.skif.service.locker.DBLockerService;
import no.statkart.skif.service.module.server.RunOnServerServiceModule;
import no.statkart.skif.service.module.server.ServerModule;
import no.statkart.skif.service.module.server.ServerServiceModule;
import no.statkart.skif.service.module.server.ServerServiceModuleStrategy;
import no.statkart.skif.service.scope.ServiceRequestScoped;
import no.statkart.skif.service.sequence.IdService;
import no.statkart.skif.service.sequence.IdServiceImpl;
import no.statkart.skif.service.sequence.SequenceBlockAllocatorService;
import no.statkart.skif.service.test.TestdataService;
import no.statkart.skif.store.*;
import no.statkart.skif.store.module.StoreServerModuleStrategyFactory;
import no.statkart.skif.store.persistence.*;
import no.statkart.skif.store.persistence.hibernate.*;
import no.statkart.skif.store.persistence.hibernate.type.EnumKodeIdType;
import no.statkart.skif.store.persistence.jdbc.ConnectionManagerUsingHibernate;
import no.statkart.skif.store.persistence.jdbc.ConnectionSelectorUsingHibernate;
import no.statkart.skif.store.persistence.kodeliste.DefaultKodelistePersistenceSessionSubtypeHandler;
import no.statkart.skif.store.persistence.kodeliste.EnumKodelisteManager;
import no.statkart.skif.store.relation.cache.RelationCacheProxyHandler;
import no.statkart.skif.store.relation.cache.StoreRelationCache;
import no.statkart.skif.store.service.StoreService;
import no.statkart.skif.store.service.ejb.EJBResourceProxyHandlerForHibernateWithLocks;
import no.statkart.skif.storetest.domain.basic.*;
import no.statkart.skif.storetest.domain.component.composite.BubbleWithCompositeComponent;
import no.statkart.skif.storetest.domain.component.entity.BubbleWithEntityComponent;
import no.statkart.skif.storetest.domain.component.entity.BubbleWithEntityInCompositeComponent;
import no.statkart.skif.storetest.domain.component.historikk.HistorikkBubbleWithEntityComponents;
import no.statkart.skif.storetest.domain.component.historikk.HistorikkBubbleWithListEntityComponents;
import no.statkart.skif.storetest.domain.demo.*;
import no.statkart.skif.storetest.domain.demo.koder.*;
import no.statkart.skif.storetest.domain.endringslogg.Endring;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLong;
import no.statkart.skif.storetest.domain.koder.*;
import no.statkart.skif.storetest.domain.mockup.Foo;
import no.statkart.skif.storetest.domain.mockup.Raz;
import no.statkart.skif.storetest.domain.multikobling.Multirefererende;
import no.statkart.skif.storetest.domain.multikobling_old.Person;
import no.statkart.skif.storetest.domain.multikobling_old.Rettsstiftelse;
import no.statkart.skif.storetest.domain.relation.uni.component.entity.X2AAWithEntityComponent;
import no.statkart.skif.storetest.domain.relation.uni.component.entity.X2BBOne;
import no.statkart.skif.storetest.domain.relation.uni.component.entity.X2CCMany;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1AA;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1BBOne;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1CCMany;
import no.statkart.skif.storetest.domain.standalone.*;
import no.statkart.skif.storetest.endringslogg.EndringManager;
import no.statkart.skif.storetest.filter.AggregertObjektFilter;
import no.statkart.skif.storetest.filter.TestBubbleFilter;
import no.statkart.skif.storetest.filter.TestBubbleFinishFilter;
import org.hibernate.Interceptor;
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
        bind(SequenceBlockAllocatorService.class).to(no.statkart.skif.storetest.service.id.SequenceBlockAllocatorService.class);

        install(new ServerServiceModule(moduleConfiguration, new StoreTestTxManagementServices().getServices()));
        install(new ServerServiceModule(moduleConfiguration, new StoreTestGroup1Services().getServices()));
        install(new ServerServiceModule(moduleConfiguration, new StoreTestStoreServices().getServices()));
        install(new ServerServiceModule(moduleConfiguration, new StoreTestStoreUpdateServices().getServices()));
        install(new ServerServiceModule(moduleConfiguration, new StoreTestTestServices().getServices()));

        // DomainServiceFinder skal ha RelationCacheProxyHandler i CallServiceChain
        ServerServiceModule domainServiceModule = new ServerServiceModule(moduleConfiguration, new StoreTestDomainFinderServices().getServices());
        domainServiceModule.getStrategy(ServiceMode.SINGLE_VM).getCallServiceChainFactorySpecification().getCallServiceChainProxyHandlers().add(0, RelationCacheProxyHandler.class);
        domainServiceModule.getStrategy(ServiceMode.JEE).getCallServiceChainFactorySpecification().getCallServiceChainProxyHandlers().add(0, RelationCacheProxyHandler.class);
        install(domainServiceModule);


        bind(TestdataService.class).to(no.statkart.skif.storetest.service.test.TestdataService.class);

        {
            // Definer services som ikke bruker Store, men bare SQL connection
            final List<Class<?>> servicesThatOnlyUseConnection = new ArrayList<Class<?>>();
            servicesThatOnlyUseConnection.addAll(new StoreTestLocalServices().getServices());
            servicesThatOnlyUseConnection.addAll(new StoreTestSequenceBlockAllocatorServices().getServices());
            final ServerServiceModule moduleThatOnlyUseConnection = new ServerServiceModule(moduleConfiguration, servicesThatOnlyUseConnection);
            moduleThatOnlyUseConnection.getStrategy(ServiceMode.SINGLE_VM).setEjbServiceChainFactorySpecification(new EJBServiceChainFactorySpecification(EJBResourceProxyHandlerForConnection.class));
            moduleThatOnlyUseConnection.getStrategy(ServiceMode.JEE).setEjbServiceChainFactorySpecification(new EJBServiceChainFactorySpecification(EJBResourceProxyHandlerForConnection.class));
            install(moduleThatOnlyUseConnection);
        }

        bind(ConnectionManager.class).toProvider(ConnectionManagerProvider.class);
        bind(Connection.class).to(ConnectionForSnapshotVersion.class);
        bind(PersistenceSessionManager.class).toProvider(PersistenceSessionManagerProvider.class);
        bind(ConnectionForSnapshotVersion.class).toProvider(ConnectionForSnapshotVersionProvider.class);
        bind(ConnectionSelector.class).to(ConnectionSelectorUsingHibernate.class);

        bind(SkifUtil.typeLiteral(DBLockerService.class, Long.class)).to(no.statkart.skif.storetest.service.locker.DBLockerService.class);
        bind(SkifUtil.typeLiteral(DBLockerInTransactionService.class, Long.class)).to(no.statkart.skif.storetest.service.locker.DBLockerInTransactionService.class);
        bind(LockerStrategy.class).to(TransactionalLockerStrategy.class);
        bind(TransactionalLockerStrategy.class).in(ServiceRequestScoped.class);

        bind(StoreService.class).to(no.statkart.skif.storetest.service.store.StoreService.class);
        bind(Session.class).toProvider(SessionProvider.class);
        bind(PersistenceSessionForSnapshot.class).toProvider(PersistenceSessionForSnapshotProvider.class);

//        if (moduleConfiguration.getServiceMode() == ServiceMode.SINGLE_VM) {
//            install(new ServerServiceModule(moduleConfiguration, new StoreTestTestServices().getServices()));
//            bind(no.statkart.skif.service.test.TestdataService.class).to(no.statkart.skif.storetest.service.test.TestdataService.class);
//        }
    }

    @Provides
    StoreRelationCache provideStoreRelationCache(Store store) {
        return store.getInstance(StoreRelationCache.class);
    }

    @Provides
    @ServiceRequestScoped
    StoreServer provideStoreServer(PersistenceSessionManager persistenceSessionManager, Injector injector, BubbleDependencyComparator bubbleDependencyComparator, Provider<VersionFinder> versionFinderProvider, LockerStrategy lockerStrategy) {
        List<StoreSessionReadListener> readListeners = ImmutableList.<StoreSessionReadListener>of(
                new TestBubbleFilter()
        );
        List<StoreSessionWriteListener> writeListeners = ImmutableList.<StoreSessionWriteListener>of(
                new TestBubbleFilter(),
                new AggregertObjektFilter()
        );
        List<StoreSessionFinishListener> finishListeners = ImmutableList.<StoreSessionFinishListener>of(
                new TestBubbleFinishFilter(),
                injector.getInstance(EndringManager.class)
        );
        StoreServer storeServer = new StoreServer(new StoreSessionServer(persistenceSessionManager, versionFinderProvider, lockerStrategy, bubbleDependencyComparator, readListeners, writeListeners, finishListeners), injector);
        return storeServer;
    }

    @Provides
    @Singleton
    EnumKodelisteManager provideEnumKodelisteManager() {
        EnumKodelisteManager enumKodelisteManager = new EnumKodelisteManager();
        enumKodelisteManager.installStatic(AEnumKodeId.class);
        enumKodelisteManager.installStatic(BEnumKodeId.class);
        enumKodelisteManager.installStatic(SEnumKodeId.class);

        enumKodelisteManager.installStatic(SimpleEnumKodeId.class);
        enumKodelisteManager.installStatic(HistorikkEnumKodeId.class);

        enumKodelisteManager.installDynamic(SimpleLocalizedDbKodeId.class);

        return enumKodelisteManager;
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
    HibernateSessionFactoryManagerBundle provideHibernateSessionFactoryManagerBundle(Provider<IdService> idServiceProvider) {

        Configuration configuration = moduleConfiguration.getConfiguration();

        // TODO: Hent directory fra moduleConfiguration
        final String hibernateMappingDir;
        if (configuration.getString("skif.hibernateVersion", "3.2").equals("3.6")) {
            hibernateMappingDir = "no/statkart/skif/storetest/persistence/hibernate36";
        } else {
            hibernateMappingDir = "no/statkart/skif/storetest/persistence/hibernate32";
        }
        HibernateSessionFactoryBuilder hibernateSessionFactoryBuilder = new HibernateSessionFactoryBuilderImpl(hibernateMappingDir)
                // NB: Rekkefølgen er viktig. Objekter som ikke avhenger av andre må stå først
                .addResource(Simple.class)
                .addResource(BubbleWithRelation.class)
                .addResource(BubbleWithFilter.class)
                .addResource(BubbleWithValueObject.class)
                .addResource(HistSimple.class)
                .addResource(HistWithRelation.class)
                .addResourceWithSubclasses(SubTypedBubble.class, SubTypeWithPrimitive.class, SubTypeWithCollection.class)

                // Components
                .addResource(BubbleWithCompositeComponent.class)
                .addResource(BubbleWithEntityComponent.class)
                .addResource(BubbleWithEntityInCompositeComponent.class)

                .addResource(HistorikkBubbleWithEntityComponents.class)
                .addResource(HistorikkBubbleWithListEntityComponents.class)

                // Multikobling
                .addResource(Multirefererende.class)

                // Koder
                .addResource(EnumKodeIdType.class)
                .addResourceWithSubclasses(HistoriskDbKode.class, SimpleLocalizedDbKode.class)

                // Gamle koder
                .addResource(ADbKode.class)
                .addResource(BDbKode.class)
                .addResourceWithSubclasses(CDbKode.class, C1DbKode.class, C2DbKode.class)
                .addResource(XStrDbKode.class)
                .addResource(StoreTestKodelisteLong.class)
                .addResource(BubbleWithKode.class)

                // Klasser for relasjonstesting
                .addResource(X1BBOne.class)
                .addResource(X1CCMany.class)
//                .addResource(X1DDUnique.class)
                .addResource(X1AA.class)

                .addResource(X2BBOne.class)
                .addResource(X2CCMany.class)
                .addResource(X2AAWithEntityComponent.class)

                .addResource(TestBubble.class)
                .addResourceUseSameIndex(SelfBubble.class)   // Blir sortert sammen me TestBubble
                .addResource(ChildBubble.class)
                .addResource(ParentBubble.class)
                .addResource(FilteredBubble.class)
                .addResource(Foo.class)
//                .addResource(Baz.class)
                .addResource(Raz.class)
//                .addResource(Bar.class)
//                .addResource(BarFoos.class)
                .addResource(TestMap.class)
                .addResource(AggregertObjekt.class)
                .addResource(Person.class)
                .addResource(Rettsstiftelse.class)
                .addResource(BubbleWithList.class)
                .addResource(BubbleWithComponents.class)

                .addResource(Endring.class)
                ;

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

            String datasourceOld = configuration.getString(SkifConfigConstants.DB_DATASOURCE_OLD, "no.statkart.matrikkel.persistens.MatrikkelOld_DS");
            hibernatePropertiesOld = ConfigurationConverter.getProperties(new PropertiesConfiguration("no/statkart/skif/storetest/config/persistence/skiftest-hibernate-server.properties"));
            hibernatePropertiesOld.setProperty(Environment.DATASOURCE, datasourceOld);
        }

        final HibernateStoreInterceptorFactory hibernateInterceptorFactory = new HibernateStoreInterceptorFactory();
        HibernateSessionFactoryManagerBundle hibernateSessionFactoryManagerBundle = new HibernateSessionFactoryManagerBundle(hibernateSessionFactoryBuilder, idServiceProvider,
                new HibernateSessionFactoryDescriptor("CURRENT(HISTORIC-SCHEMA)", new SnapshotVersionSeed(SnapshotVersion.CURRENT), true, false, hibernatePropertiesCurrent, hibernateInterceptorFactory),
                new HibernateSessionFactoryDescriptor("OLD(HISTORIC-SCHEMA)", new SnapshotVersionSeed(SnapshotVersion.OLD), true, true, hibernatePropertiesOld, hibernateInterceptorFactory)
        );

        return hibernateSessionFactoryManagerBundle;

    }

    @Provides
    @ServiceRequestScoped
    ResourceManager provideResourceManager(Provider<ResourceManagerConfigurator> resourceManagerConfiguratorProvider, Provider<HibernateSessionFactoryManagerBundle> hibernateSessionFactoryManagerBundleProvider, Provider<EnumKodelisteManager> enumKodelisteManagerProvider) {
        final String strategy = resourceManagerConfiguratorProvider.get().getStrategy();
        if (strategy == ResourceManagerConfigurator.CONNECTION_ONLY) {
            return createResourceManagerForConnectionOnlyStrategy();
        } else {
            return createResourceManagerForHibernateStrategy(
                    hibernateSessionFactoryManagerBundleProvider.get(),
                    enumKodelisteManagerProvider.get()
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

    ResourceManager createResourceManagerForHibernateStrategy(HibernateSessionFactoryManagerBundle hibernateSessionFactoryManagerBundle, EnumKodelisteManager enumKodelisteManager) {
        HibernatePersistenceSessionMasterImpl persistenceSessionMasterCurrent = new DefaultHibernatePersistenceSessionImplExt(
                hibernateSessionFactoryManagerBundle.getBundle().get(0)
        );

        HibernatePersistenceSessionMasterImpl persistenceSessionMasterOld = new DefaultHibernatePersistenceSessionImplExt(
                hibernateSessionFactoryManagerBundle.getBundle().get(1)
        );

        PersistenceSessionManager persistenceSessionManager = new DefaultPersistenceSessionManager(
                new DefaultPersistenceSessionStrategy(
                        persistenceSessionMasterCurrent,
                        new DefaultKodelistePersistenceSessionSubtypeHandler(persistenceSessionMasterCurrent, enumKodelisteManager)
                ),
                new DefaultPersistenceSessionStrategy(
                        persistenceSessionMasterOld,
                        new DefaultKodelistePersistenceSessionSubtypeHandler(persistenceSessionMasterOld, enumKodelisteManager)
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

