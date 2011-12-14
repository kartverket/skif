package no.statkart.skif.storetest.config;

import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.SkifModule;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.module.StrategyTuple;
import no.statkart.skif.service.chain.EJBServiceChainFactoryWithTxSpecification;
import no.statkart.skif.service.module.server.RunOnServerServiceModule;
import no.statkart.skif.service.module.server.ServerModule;
import no.statkart.skif.service.module.server.ServerServiceModule;
import no.statkart.skif.service.module.server.ServerServiceModuleStrategy;
import no.statkart.skif.service.scope.ServiceRequestScoped;
import no.statkart.skif.store.*;
import no.statkart.skif.store.module.NonhistStoreServerModuleStrategyFactory;
import no.statkart.skif.store.module.server.NonhistServerStoreModule;
import no.statkart.skif.store.persistence.hibernate.HibernateStoreSessionManager;
import no.statkart.skif.store.persistence.hibernate.HibernateStoreSessionPersister;
import no.statkart.skif.store.persistence.hibernate.StoreHibernateSessionFactoryBuilder;
import no.statkart.skif.store.persistence.hibernate.type.EnumKodeIdType;
import no.statkart.skif.store.persistence.kodeliste.DbKodelisteLoader;
import no.statkart.skif.store.persistence.kodeliste.KodelisteManager;
import no.statkart.skif.store.persistence.kodeliste.KodelistePersister;
import no.statkart.skif.store.service.ejb.EJBResourceProxyHandlerForHibernate;
import no.statkart.skif.store.service.ejb.EJBResourceProxyHandlerForHibernateWithLocks;
import no.statkart.skif.storetest.domain.nonhist.Bar;
import no.statkart.skif.storetest.domain.nonhist.Baz;
import no.statkart.skif.storetest.domain.nonhist.Foo;
import no.statkart.skif.storetest.domain.nonhist.koder.CDbKode;
import no.statkart.skif.storetest.persistence.StoreTestKodelisteLoader;
import no.statkart.skif.storetest.persistence.StoreTestStorePersisterStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class NonhistTestServerModule extends SkifModule {
    public NonhistTestServerModule(Configuration configuration) {
        super(configuration);
    }

    @Override
    protected ModuleStrategyFactory defineDefaultModuleStrategyFactory() {
        // Konfigurer EJBServiceChain til å bruke en factory som har en ProxyHandler for transaksjonshåndtering
        ModuleStrategyFactory factory = new NonhistStoreServerModuleStrategyFactory();
        StrategyTuple<ServerServiceModuleStrategy> prototype = factory.getPrototype(ServerServiceModule.class);
        prototype.getStrategy(ServiceMode.SINGLE_VM).setEjbServiceChainFactorySpecification(new EJBServiceChainFactoryWithTxSpecification(EJBResourceProxyHandlerForHibernate.class));
        prototype.getStrategy(ServiceMode.JEE).setEjbServiceChainFactorySpecification(new EJBServiceChainFactoryWithTxSpecification(EJBResourceProxyHandlerForHibernate.class));

        return factory;
    }

    @Override
    protected void configure() {
        install(new ServerModule(moduleConfiguration));
        install(new RunOnServerServiceModule(moduleConfiguration));

        NonhistServerStoreModule serverStoreModule = new NonhistServerStoreModule(moduleConfiguration, no.statkart.skif.storetest.service.store.StoreService.class, no.statkart.skif.storetest.service.locker.DBLockerService.class, no.statkart.skif.storetest.service.locker.DBLockerInTransactionService.class, "no/statkart/skif/storetest/persistence/hibernate") {
            @Override
            protected void configureHibernate(StoreHibernateSessionFactoryBuilder factoryBuilder) {
                // NB: Rekkefølgen er viktig. Objekter som ikke avhenger av andre må stå først
                factoryBuilder.addResource(no.statkart.skif.store.persistence.hibernate.type.EnumKodeIdType.class);
                factoryBuilder.addResource(no.statkart.skif.storetest.domain.demo.koder.CDbKode.class);
                factoryBuilder.addResource(no.statkart.skif.storetest.domain.demo.Foo.class);
                factoryBuilder.addResource(no.statkart.skif.storetest.domain.demo.Bar.class);
                factoryBuilder.addResource(no.statkart.skif.storetest.domain.demo.Baz.class);
                factoryBuilder.addResource(EnumKodeIdType.class);
                factoryBuilder.addResource(CDbKode.class);
//                factoryBuilder.addResourceUsingAbsolutePath(EnumKodeIdType.class, "no/statkart/skif/storetest/persistence/hibernate/EnumKodeIdType2.hbm.xml");
                factoryBuilder.addResource(Foo.class);
                factoryBuilder.addResource(Baz.class);
                factoryBuilder.addResource(Bar.class);
            }
        };
        serverStoreModule.getStrategy(ServiceMode.JEE).setHibernateConfigurationFilename("no/statkart/skif/storetest/config/persistence/skiftest-hibernate-server.properties");
        serverStoreModule.getStrategy(ServiceMode.SINGLE_VM).setHibernateConfigurationFilename("no/statkart/skif/storetest/config/persistence/skiftest-hibernate-singlevm.properties");
        install(serverStoreModule);

        bind(Store.class).to(StoreServer.class);
        bind(DbKodelisteLoader.class).to(StoreTestKodelisteLoader.class);


//        install(new ServerServiceModule(moduleConfiguration, new StoreTestStoreServices().getServices()));
        install(new ServerServiceModule(moduleConfiguration, new StoreTestLocalServices().getServices()));
        install(new ServerServiceModule(moduleConfiguration, new NonhistTestServices().getServices()));
    }

    @Provides
    @Singleton
    KodelisteManager bubbleKodelisteManagerProvider() {
        KodelisteManager kodelisteManager = new KodelisteManager();
//        kodelisteManager.installStatic(SEnumKodeId.class);
        return kodelisteManager;
    }


    @Provides
    @ServiceRequestScoped
    StoreServer storeProvider(HibernateStoreSessionManager hibernateStoreSessionManager, HashStorePersister hashStorePersister, KodelistePersister kodelistePersister, Injector injector) {
        HibernateStoreSessionPersister hibernateStoreSessionPersister = new HibernateStoreSessionPersister(hibernateStoreSessionManager);

        StorePersisterStrategy storePersisterStrategy = new StoreTestStorePersisterStrategy(hibernateStoreSessionPersister, hashStorePersister, kodelistePersister);

        AbstractStoreSessionAuthorizerChain authorizerChain = new AbstractStoreSessionAuthorizerChain() {
            @Override
            public <T extends BubbleObject> void maskFields(StoreEntry<T> storeEntry) {
                T bubbleObject = storeEntry.getBubbleObject();
/*
                if (bubbleObject instanceof TSubMaskedBubble) {
                    TSubMaskedBubble copy = (TSubMaskedBubble) CopyHelper.copy(bubbleObject);
                    copy.setMaskedField("masked-field");
                    storeEntry.setBubbleObject((T)copy);
                }
*/
            }
        };

        StoreCache storeCache = new StoreCache();
        StoreSessionChain[] storeChainList = {
                authorizerChain,
                new StoreSessionCacheChain(),
                new StoreSessionPersisterChain(storePersisterStrategy, null)

        };
        StoreServer store = new StoreServer(storeCache, storeChainList);
        injector.injectMembers(store);
        store.init();

        return store;
    }

    @Provides
    @Singleton
    HashStorePersister storePersisterProvider() {
        Map<BubbleId<?>, BubbleObject> storeMap = new HashMap<BubbleId<?>, BubbleObject>();

        HashStorePersister storePersister = new HashStorePersister(storeMap);
        return storePersister;
    }

    private <T extends BubbleObject, I extends BubbleId<? extends T>> T addToMap(I id, Map<BubbleId<?>, BubbleObject> storeMap) {
        T bubble = createBubble(id);
        storeMap.put(bubble.getId(), bubble);
        return bubble;
    }

    private <T extends BubbleObject, I extends BubbleId<? extends T>> T createBubble(I id) {
        T bubble = id.createTypeInstance();
        bubble.setId(id);
        return (T) bubble;
    }

}

