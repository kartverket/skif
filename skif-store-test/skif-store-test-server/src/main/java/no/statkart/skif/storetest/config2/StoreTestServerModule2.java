package no.statkart.skif.storetest.config2;

import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.SkifModule;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.module.StrategyTuple;
import no.statkart.skif.service.chain.EJBServiceChainFactoryWithTxSpecification;
import no.statkart.skif.service.module.server.ServerModule;
import no.statkart.skif.service.module.server.ServerServiceModule;
import no.statkart.skif.service.module.server.ServerServiceModuleStrategy;
import no.statkart.skif.service.scope.ServiceRequestScoped;
import no.statkart.skif.store2.*;
import no.statkart.skif.store2.module.StoreServerModuleStrategyFactory2;
import no.statkart.skif.store2.module.server.ServerStoreModule2;
import no.statkart.skif.store2.persistence.hibernate.HibernateStoreSession2;
import no.statkart.skif.store2.persistence.hibernate.StoreHibernateSessionFactoryBuilder2;
import no.statkart.skif.store2.persistence.kodeliste.DbKodelisteLoader2;
import no.statkart.skif.store2.persistence.kodeliste.KodelisteManager2;
import no.statkart.skif.store2.persistence.kodeliste.KodelistePersister2;
import no.statkart.skif.store2.service.ejb.EJBResourceProxyHandlerForHibernate2;
import no.statkart.skif.storetest.domain2.*;
import no.statkart.skif.storetest.persistence2.StoreTestKodelisteLoader2;
import no.statkart.skif.storetest.persistence2.StoreTestStorePersisterStrategy2;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class StoreTestServerModule2 extends SkifModule {
    public StoreTestServerModule2(Configuration configuration) {
        super(configuration);
    }

    @Override
    protected ModuleStrategyFactory defineDefaultModuleStrategyFactory() {
        // Konfigurer EJBServiceChain til å bruke en factory som har en ProxyHandler for transaksjonshåndtering
        ModuleStrategyFactory factory = new StoreServerModuleStrategyFactory2();
        StrategyTuple<ServerServiceModuleStrategy> prototype = factory.getPrototype(ServerServiceModule.class);
        prototype.getStrategy(ServiceMode.SINGLE_VM).setEjbServiceChainFactorySpecification(new EJBServiceChainFactoryWithTxSpecification(EJBResourceProxyHandlerForHibernate2.class));
        prototype.getStrategy(ServiceMode.JEE).setEjbServiceChainFactorySpecification(new EJBServiceChainFactoryWithTxSpecification(EJBResourceProxyHandlerForHibernate2.class));

        return factory;
    }

    @Override
    protected void configure() {
        install(new ServerModule(moduleConfiguration));

        ServerStoreModule2 serverStoreModule = new ServerStoreModule2(moduleConfiguration, "no/statkart/skif/storetest/persistence2/hibernate") {
            @Override
            protected void configureHibernate(StoreHibernateSessionFactoryBuilder2 facotryBuilder) {
                facotryBuilder.addResource(TestBubble2.class);
                facotryBuilder.addResource(TestMap2.class);
            }
        };
        serverStoreModule.getStrategy(ServiceMode.JEE).setHibernateConfigurationFilename("no/statkart/skif/storetest/config/persistence/skiftest-hibernate-server.properties");
        serverStoreModule.getStrategy(ServiceMode.SINGLE_VM).setHibernateConfigurationFilename("no/statkart/skif/storetest/config/persistence/skiftest-hibernate-singlevm.properties");
        install(serverStoreModule);

        bind(Store2.class).to(StoreServer2.class);
        bind(DbKodelisteLoader2.class).to(StoreTestKodelisteLoader2.class);


        install(new ServerServiceModule(moduleConfiguration, new StoreTestGroup1Services2().getServices()));
        install(new ServerServiceModule(moduleConfiguration, new StoreTestStoreServices2().getServices()));
    }

    @Provides
    @Singleton
    KodelisteManager2 bubbleKodelisteManagerProvider() {
        KodelisteManager2 kodelisteManager = new KodelisteManager2();
        kodelisteManager.installStatic(TestAEnumKodeId2.class);
        kodelisteManager.installStatic(TestBEnumKodeId2.class);
        kodelisteManager.installStatic(TestCEnumKodeId2.class);
        return kodelisteManager;
    }


    @Provides
    @ServiceRequestScoped
    StoreServer2 storeProvider(HibernateStoreSession2 hibernatePersister, HashStorePersister2 hashStorePersister, KodelistePersister2 kodelistePersister, Injector injector) {
        StorePersisterStrategy2 storePersisterStrategy = new StoreTestStorePersisterStrategy2(hibernatePersister, hashStorePersister,kodelistePersister) ;

        AbstractStoreSessionAuthorizerChain2 authorizerChain = new AbstractStoreSessionAuthorizerChain2() {
            @Override
            public <T extends BubbleObject2> void maskFields(StoreEntry2<T> storeEntry) {
                T bubbleObject = storeEntry.getBubbleObject();
/*
                if (bubbleObject instanceof TSubMaskedBubble) {
                    TSubMaskedBubble copy = (TSubMaskedBubble) CopyHelper.copy(bubbleObject);
                    copy.setMaskedField("masked-field");
                    storeEntry.setBubbleObject2((T)copy);
                }
*/
            }
        };

        StoreCache2 storeCache = new StoreCache2();
        StoreSessionChain2[] storeChainList = {
                authorizerChain,
                new StoreSessionCacheChain2(),
                new StoreSessionPersisterChain2(storePersisterStrategy, null)

        };
        StoreServer2 store = new StoreServer2(storeCache, storeChainList);
        injector.injectMembers(store);
        store.init();

        return store;
    }

    @Provides
    @Singleton
    HashStorePersister2 storePersisterProvider() {
        Map<BubbleId2<?>, BubbleObject2> storeMap = new HashMap<BubbleId2<?>, BubbleObject2>();
        addToMap(new TestBubbleId2(1), storeMap);
        addToMap(new TestBubbleId2(2), storeMap);
        addToMap(new TestBubbleId2(3), storeMap);

        HashStorePersister2 storePersister = new HashStorePersister2(storeMap);
        return storePersister;
    }

    private <T extends BubbleObject2, I extends BubbleId2<? extends T>> T addToMap(I id, Map<BubbleId2<?>, BubbleObject2> storeMap) {
        T bubble = createBubble(id);
        storeMap.put(bubble.getId(), bubble);
        return bubble;
    }

    private <T extends BubbleObject2, I extends BubbleId2<? extends T>> T createBubble(I id) {
        T bubble = id.createTypeInstance();
        bubble.setId(id);
        return (T) bubble;
    }

}

