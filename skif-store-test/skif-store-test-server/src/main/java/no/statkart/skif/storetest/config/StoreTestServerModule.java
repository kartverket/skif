package no.statkart.skif.storetest.config;

import com.google.common.collect.ImmutableList;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.SkifModule;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.config.SkifConfigConstants;
import no.statkart.skif.exception.ConfigurationException;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.module.StrategyTuple;
import no.statkart.skif.persistence.DefaultResourceManager;
import no.statkart.skif.persistence.HistorikkTransactionTimeServiceImpl;
import no.statkart.skif.persistence.ResourceManager;
import no.statkart.skif.persistence.ResourceManagerConfigurator;
import no.statkart.skif.persistence.TransactionTimeService;
import no.statkart.skif.persistence.VersionFinder;
import no.statkart.skif.persistence.jdbc.ConnectionFactoryUsingDataSource;
import no.statkart.skif.persistence.jdbc.ConnectionFactoryUsingPool;
import no.statkart.skif.persistence.jdbc.ConnectionForSnapshotVersion;
import no.statkart.skif.persistence.jdbc.ConnectionForSnapshotVersionProvider;
import no.statkart.skif.persistence.jdbc.ConnectionManager;
import no.statkart.skif.persistence.jdbc.ConnectionManagerProvider;
import no.statkart.skif.persistence.jdbc.ConnectionManagerUsingFactory;
import no.statkart.skif.persistence.jdbc.ConnectionSelector;
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
import no.statkart.skif.store.BubbleDependencyComparator;
import no.statkart.skif.store.BubbleModelConfiguration;
import no.statkart.skif.store.LockerStrategy;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionSeed;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.StoreServer;
import no.statkart.skif.store.StoreSessionFinishListener;
import no.statkart.skif.store.StoreSessionReadListener;
import no.statkart.skif.store.StoreSessionServer;
import no.statkart.skif.store.StoreSessionWriteListener;
import no.statkart.skif.store.TransactionalLockerStrategy;
import no.statkart.skif.store.module.StoreServerModuleStrategyFactory;
import no.statkart.skif.store.module.common.RemoteServiceModuleStrategyWithServiceContextSVMapper;
import no.statkart.skif.store.persistence.DefaultPersistenceSessionManager;
import no.statkart.skif.store.persistence.DefaultPersistenceSessionStrategy;
import no.statkart.skif.store.persistence.PersistenceSessionForSnapshot;
import no.statkart.skif.store.persistence.PersistenceSessionForSnapshotProvider;
import no.statkart.skif.store.persistence.PersistenceSessionManager;
import no.statkart.skif.store.persistence.PersistenceSessionManagerProvider;
import no.statkart.skif.store.persistence.hibernate.DefaultHibernateSessionFactoryManagerBundle;
import no.statkart.skif.store.persistence.hibernate.HibernateInterceptorFactory;
import no.statkart.skif.store.persistence.hibernate.HibernatePersistenceSessionMasterImpl;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryBuilder;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryDescriptor;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryManagerBundle;
import no.statkart.skif.store.persistence.hibernate.HibernateStoreInterceptor;
import no.statkart.skif.store.persistence.hibernate.HibernateStoreInterceptorFactory;
import no.statkart.skif.store.persistence.hibernate.SessionProvider;
import no.statkart.skif.store.persistence.hibernate.type.EnumKodeIdType;
import no.statkart.skif.store.persistence.jdbc.ConnectionManagerUsingHibernate;
import no.statkart.skif.store.persistence.jdbc.ConnectionSelectorUsingHibernate;
import no.statkart.skif.store.persistence.kodeliste.DefaultKodelistePersistenceSessionSubtypeHandler;
import no.statkart.skif.store.persistence.kodeliste.EnumKodelisteManager;
import no.statkart.skif.store.relation.cache.RelationCacheProxyHandler;
import no.statkart.skif.store.relation.cache.StoreRelationCache;
import no.statkart.skif.store.service.StoreService;
import no.statkart.skif.store.service.ejb.EJBResourceProxyHandlerForHibernateWithLocks;
import no.statkart.skif.storetest.domain.basic.BubbleWithAnyBubbleRef;
import no.statkart.skif.storetest.domain.basic.BubbleWithFilter;
import no.statkart.skif.storetest.domain.basic.BubbleWithKode;
import no.statkart.skif.storetest.domain.basic.BubbleWithLocalDate;
import no.statkart.skif.storetest.domain.basic.BubbleWithLocalDateTime;
import no.statkart.skif.storetest.domain.basic.BubbleWithRelation;
import no.statkart.skif.storetest.domain.basic.BubbleWithValueObject;
import no.statkart.skif.storetest.domain.basic.HistSimple;
import no.statkart.skif.storetest.domain.basic.HistWithRelation;
import no.statkart.skif.storetest.domain.basic.Simple;
import no.statkart.skif.storetest.domain.basic.SubTypeWithCollection;
import no.statkart.skif.storetest.domain.basic.SubTypeWithPrimitive;
import no.statkart.skif.storetest.domain.basic.SubTypedBubble;
import no.statkart.skif.storetest.domain.basic.TestMap;
import no.statkart.skif.storetest.domain.component.composite.BubbleWithCompositeComponent;
import no.statkart.skif.storetest.domain.component.entity.BubbleWithEntityComponent;
import no.statkart.skif.storetest.domain.component.entity.BubbleWithEntityInCompositeComponent;
import no.statkart.skif.storetest.domain.component.historikk.HistorikkBubbleWithEntityComponents;
import no.statkart.skif.storetest.domain.component.historikk.HistorikkBubbleWithListEntityComponents;
import no.statkart.skif.storetest.domain.demo.AggregertObjekt;
import no.statkart.skif.storetest.domain.demo.BubbleWithComponents;
import no.statkart.skif.storetest.domain.demo.BubbleWithList;
import no.statkart.skif.storetest.domain.demo.koder.ADbKode;
import no.statkart.skif.storetest.domain.demo.koder.AEnumKodeId;
import no.statkart.skif.storetest.domain.demo.koder.BDbKode;
import no.statkart.skif.storetest.domain.demo.koder.BEnumKodeId;
import no.statkart.skif.storetest.domain.demo.koder.C1DbKode;
import no.statkart.skif.storetest.domain.demo.koder.C2DbKode;
import no.statkart.skif.storetest.domain.demo.koder.CDbKode;
import no.statkart.skif.storetest.domain.demo.koder.SEnumKodeId;
import no.statkart.skif.storetest.domain.demo.koder.XStrDbKode;
import no.statkart.skif.storetest.domain.endringslogg.Endring;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLong;
import no.statkart.skif.storetest.domain.koder.HistorikkEnumKodeId;
import no.statkart.skif.storetest.domain.koder.HistoriskDbKode;
import no.statkart.skif.storetest.domain.koder.SimpleEnumKodeId;
import no.statkart.skif.storetest.domain.koder.SimpleLocalizedDbKode;
import no.statkart.skif.storetest.domain.koder.SimpleLocalizedDbKodeId;
import no.statkart.skif.storetest.domain.mockup.Foo;
import no.statkart.skif.storetest.domain.mockup.Raz;
import no.statkart.skif.storetest.domain.multikobling.Multirefererende;
import no.statkart.skif.storetest.domain.multikobling.entity.BubbleWithEntityInMultikobling;
import no.statkart.skif.storetest.domain.multikobling_old.Person;
import no.statkart.skif.storetest.domain.multikobling_old.Rettsstiftelse;
import no.statkart.skif.storetest.domain.relation.uni.component.entity.X2AAWithEntityComponent;
import no.statkart.skif.storetest.domain.relation.uni.component.entity.X2BBOne;
import no.statkart.skif.storetest.domain.relation.uni.component.entity.X2CCMany;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1AA;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1BBOne;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1CCMany;
import no.statkart.skif.storetest.domain.standalone.ChildBubble;
import no.statkart.skif.storetest.domain.standalone.ChildBubbleEmptyColOptimizer;
import no.statkart.skif.storetest.domain.standalone.FilteredBubble;
import no.statkart.skif.storetest.domain.standalone.ParentBubble;
import no.statkart.skif.storetest.domain.standalone.ParentBubbleEmptyColOptimizer;
import no.statkart.skif.storetest.domain.standalone.ParentBubbleEmptyColOptimizerSub1;
import no.statkart.skif.storetest.domain.standalone.SelfBubble;
import no.statkart.skif.storetest.domain.standalone.TestBubble;
import no.statkart.skif.storetest.endringslogg.EndringManager;
import no.statkart.skif.storetest.filter.AggregertObjektFilter;
import no.statkart.skif.storetest.filter.TestBubbleFilter;
import no.statkart.skif.storetest.filter.TestBubbleFinishFilter;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Environment;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
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
        ModuleStrategyFactory factory = new StoreServerModuleStrategyFactory(RemoteServiceModuleStrategyWithServiceContextSVMapper.class);
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
            final List<Class<?>> servicesThatOnlyUseConnection = new ArrayList<>();
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

        bind(TransactionTimeService.class).to(HistorikkTransactionTimeServiceImpl.class);

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
        return store.getRelationCache();
    }

    @Provides
    @ServiceRequestScoped
    StoreServer provideStoreServer(PersistenceSessionManager persistenceSessionManager, Injector injector, BubbleDependencyComparator bubbleDependencyComparator, Provider<VersionFinder> versionFinderProvider, Provider<SnapshotVersion> snapshotVersionProvider, LockerStrategy lockerStrategy) {
        List<StoreSessionReadListener> readListeners = ImmutableList.of(
                new TestBubbleFilter()
        );
        List<StoreSessionWriteListener> writeListeners = ImmutableList.of(
                new TestBubbleFilter(),
                new AggregertObjektFilter()
        );
        List<StoreSessionFinishListener> finishListeners = ImmutableList.of(
                new TestBubbleFinishFilter(),
                injector.getInstance(EndringManager.class)
        );
        //noinspection UnnecessaryLocalVariable
        StoreServer storeServer = new StoreServer(new StoreSessionServer(persistenceSessionManager, versionFinderProvider, snapshotVersionProvider, lockerStrategy, bubbleDependencyComparator, readListeners, writeListeners, finishListeners), injector);
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
        return HibernateStoreInterceptor::new;
    }

    @Provides
    @Singleton
    BubbleModelConfiguration provideBubbleClassDependencyIndex() {
        return new BubbleModelConfiguration()
                // NB: Rekkefølgen er viktig. Objekter som ikke avhenger av andre må stå først
                .addBubble(Simple.class)
                .addBubble(BubbleWithRelation.class)
                .addBubble(BubbleWithAnyBubbleRef.class)
                .addBubble(BubbleWithFilter.class)
                .addBubble(BubbleWithValueObject.class)
                .addBubble(BubbleWithLocalDate.class)
                .addBubble(BubbleWithLocalDateTime.class)
                .addBubble(HistSimple.class)
                .addBubble(HistWithRelation.class)
                .addBubbleWithSubclasses(SubTypedBubble.class, SubTypeWithPrimitive.class, SubTypeWithCollection.class)

                // Components
                .addBubble(BubbleWithCompositeComponent.class)
                .addBubble(BubbleWithEntityComponent.class)
                .addBubble(BubbleWithEntityInCompositeComponent.class)

                .addBubble(HistorikkBubbleWithEntityComponents.class)
                .addBubble(HistorikkBubbleWithListEntityComponents.class)

                // Multikobling (standard)
                .addBubble(Multirefererende.class)

                // Multikobling (entity)
                .addBubble(BubbleWithEntityInMultikobling.class)

                // Koder
                .addBubbleWithSubclasses(HistoriskDbKode.class, SimpleLocalizedDbKode.class)

                // Gamle koder
                .addBubble(ADbKode.class)
                .addBubble(BDbKode.class)
                .addBubbleWithSubclasses(CDbKode.class, C1DbKode.class, C2DbKode.class)
                .addBubble(XStrDbKode.class)
                .addBubble(StoreTestKodelisteLong.class)
                .addBubble(BubbleWithKode.class)

                // Klasser for relasjonstesting
                .addBubble(X1BBOne.class)
                .addBubble(X1CCMany.class)
//                .addBubble(X1DDUnique.class)
                .addBubble(X1AA.class)

                .addBubble(X2BBOne.class)
                .addBubble(X2CCMany.class)
                .addBubble(X2AAWithEntityComponent.class)

                .addBubble(TestBubble.class)
                .addBubbleUseSameIndex(SelfBubble.class)   // Blir sortert sammen me TestBubble
                .addBubble(ChildBubble.class)
                .addBubble(ParentBubble.class)
                .addBubble(FilteredBubble.class)
                .addBubble(Foo.class)
//                .addBubble(Baz.class)
                .addBubble(Raz.class)
//                .addBubble(Bar.class)
//                .addBubble(BarFoos.class)
                .addBubble(AggregertObjekt.class)
                .addBubble(Person.class)
                .addBubble(Rettsstiftelse.class)
                .addBubble(BubbleWithList.class)
                .addBubble(BubbleWithComponents.class)
                .addBubble(ChildBubbleEmptyColOptimizer.class)
                .addBubbleWithSubclasses(ParentBubbleEmptyColOptimizer.class, ParentBubbleEmptyColOptimizerSub1.class)

                .addBubble(Endring.class);
    }

    @Provides
    @Singleton
    HibernateSessionFactoryManagerBundle provideHibernateSessionFactoryManagerBundle(Provider<IdService> idServiceProvider, Provider<DataSource> poolProvider, BubbleModelConfiguration bubbleModelConfiguration) {

        Configuration configuration = moduleConfiguration.getConfiguration();

        // TODO: Hent directory fra moduleConfiguration
        final String hibernateMappingDir = "no/statkart/skif/storetest/persistence/hibernate";
        HibernateSessionFactoryBuilder hibernateSessionFactoryBuilder = new HibernateSessionFactoryBuilder(hibernateMappingDir)
                .addResource(EnumKodeIdType.class)
                .addResource(TestMap.class)
                .addBubbleModel(bubbleModelConfiguration)
                ;

        PropertiesConfiguration hibernatePropertiesConfiguration = new PropertiesConfiguration("no/statkart/skif/storetest/config/persistence/skiftest-hibernate.properties");

        Properties hibernatePropertiesCurrent;
        Properties hibernatePropertiesOld;
        if (moduleConfiguration.getServiceMode() == ServiceMode.SINGLE_VM) {
            hibernatePropertiesConfiguration.setProperty(AvailableSettings.TRANSACTION_COORDINATOR_STRATEGY, "jdbc");
            hibernatePropertiesCurrent = ConfigurationConverter.getProperties(hibernatePropertiesConfiguration);

            hibernatePropertiesCurrent.setProperty(Environment.CONNECTION_PROVIDER, no.statkart.skif.persistence.hibernate.PoolConnectionProvider.class.getName());
            hibernatePropertiesCurrent.put(Environment.DATASOURCE, poolProvider.get());

            hibernatePropertiesOld = hibernatePropertiesCurrent;
        } else {
            hibernatePropertiesConfiguration.setProperty(AvailableSettings.TRANSACTION_COORDINATOR_STRATEGY, "jta");
            if ("org.apache.openejb.core.OpenEJBInitialContextFactory".equals(System.getProperty("java.naming.factory.initial"))) {
                hibernatePropertiesConfiguration.setProperty(AvailableSettings.JTA_PLATFORM, "no.statkart.skif.util.TomEEJtaPlatform");
            } else {
                hibernatePropertiesConfiguration.setProperty(AvailableSettings.JTA_PLATFORM, "org.hibernate.engine.transaction.jta.platform.internal.WeblogicJtaPlatform");
            }
            hibernatePropertiesCurrent = ConfigurationConverter.getProperties(hibernatePropertiesConfiguration);

            String datasourceCurrent = configuration.getString(SkifConfigConstants.DB_DATASOURCE);  //denne skal finnes i default konfigurasjon (filtreres inn via gradle.properties)
            hibernatePropertiesCurrent.setProperty(Environment.DATASOURCE, datasourceCurrent);

            hibernatePropertiesOld = ConfigurationConverter.getProperties(hibernatePropertiesConfiguration);
            String datasourceOld = configuration.getString(SkifConfigConstants.DB_DATASOURCE_OLD);  //denne skal finnes i default konfigurasjon (filtreres inn via gradle.properties)
            hibernatePropertiesOld.setProperty(Environment.DATASOURCE, datasourceOld);
        }

        final HibernateStoreInterceptorFactory hibernateInterceptorFactory = new HibernateStoreInterceptorFactory();

        //noinspection UnnecessaryLocalVariable
        HibernateSessionFactoryManagerBundle hibernateSessionFactoryManagerBundle = new DefaultHibernateSessionFactoryManagerBundle(hibernateSessionFactoryBuilder, idServiceProvider,
                new HibernateSessionFactoryDescriptor("CURRENT(HISTORIC-SCHEMA)", new SnapshotVersionSeed(SnapshotVersion.CURRENT), true, false, hibernatePropertiesCurrent, hibernateInterceptorFactory),
                new HibernateSessionFactoryDescriptor("OLD(HISTORIC-SCHEMA)", new SnapshotVersionSeed(SnapshotVersion.OLD), true, true, hibernatePropertiesOld, hibernateInterceptorFactory)
        );

        return hibernateSessionFactoryManagerBundle;

    }

    @Provides
    @Singleton
    DataSource provideConnectionPool() {
        if (moduleConfiguration.getServiceMode() == ServiceMode.SINGLE_VM || moduleConfiguration.getServiceMode() == ServiceMode.SINGLE_VM_XML) {
            Configuration configuration = moduleConfiguration.getConfiguration();
            String username = configuration.getString(SkifConfigConstants.DB_USERNAME);
            String password = configuration.getString(SkifConfigConstants.DB_PASSWORD);
            String url = configuration.getString(SkifConfigConstants.DB_JDBC_URL);

            try {
                ComboPooledDataSource pool = new ComboPooledDataSource();
                pool.setDriverClass("oracle.jdbc.OracleDriver");
                pool.setJdbcUrl(url);
                pool.setUser(username);
                pool.setPassword(password);
                return pool;
            } catch (PropertyVetoException e) {
                throw new ImplementationException("Could not set up connection pool", e);
            }
        } else {
            throw new ImplementationException("Will not provide connection pool in JEE-mode. Get datasource from JNDI.");
        }
    }

    @Provides
    @ServiceRequestScoped
    ResourceManager provideResourceManager(Provider<ResourceManagerConfigurator> resourceManagerConfiguratorProvider, Provider<HibernateSessionFactoryManagerBundle> hibernateSessionFactoryManagerBundleProvider, Provider<EnumKodelisteManager> enumKodelisteManagerProvider, Provider<DataSource> dataSourceProvider) {
        final String strategy = resourceManagerConfiguratorProvider.get().getStrategy();
        if (strategy == ResourceManagerConfigurator.CONNECTION_ONLY) {
            return createResourceManagerForConnectionOnlyStrategy(dataSourceProvider);
        } else {
            return createResourceManagerForHibernateStrategy(
                    hibernateSessionFactoryManagerBundleProvider.get(),
                    enumKodelisteManagerProvider.get()
            );
        }

    }

    ResourceManager createResourceManagerForConnectionOnlyStrategy(Provider<DataSource> dataSourceProvider) {
        Configuration configuration = moduleConfiguration.getConfiguration();
        ConnectionManager connectionManager;
        if (moduleConfiguration.getServiceMode() == ServiceMode.SINGLE_VM || moduleConfiguration.getServiceMode() == ServiceMode.SINGLE_VM_XML) {
            connectionManager = new ConnectionManagerUsingFactory(new ConnectionFactoryUsingPool(dataSourceProvider.get(), false, SnapshotVersion.CURRENT, false));
        } else {
            String datasource = configuration.getString(SkifConfigConstants.DB_DATASOURCE);
            if (datasource == null) throw new ConfigurationException(String.format("Mangler verdi for %s", SkifConfigConstants.DB_DATASOURCE)); //denne skal finnes i default konfigurasjon (filtreres inn via gradle.properties)

            connectionManager = new ConnectionManagerUsingFactory(new ConnectionFactoryUsingDataSource(datasource, false, SnapshotVersion.CURRENT, false));
        }

        return new DefaultResourceManager(new ResourceManager.Entry(connectionManager, ConnectionManager.class));
    }

    ResourceManager createResourceManagerForHibernateStrategy(HibernateSessionFactoryManagerBundle hibernateSessionFactoryManagerBundle, EnumKodelisteManager enumKodelisteManager) {
        HibernatePersistenceSessionMasterImpl persistenceSessionMasterCurrent = new HibernatePersistenceSessionMasterImpl(
                hibernateSessionFactoryManagerBundle.getBundle().get(0)
        );

        HibernatePersistenceSessionMasterImpl persistenceSessionMasterOld = new HibernatePersistenceSessionMasterImpl(
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

        //noinspection UnnecessaryLocalVariable
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

