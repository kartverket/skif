package no.statkart.skif.storetest.config;

import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.SkifModule;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.service.locker.DBLockerInTransactionService;
import no.statkart.skif.service.locker.DBLockerService;
import no.statkart.skif.service.module.client.ClientModuleStrategyFactory;
import no.statkart.skif.service.module.common.RemoteServerModule;
import no.statkart.skif.service.module.common.RemoteServiceModule;
import no.statkart.skif.service.module.common.RunOnServerRemoteServiceModule;
import no.statkart.skif.service.sequence.IdService;
import no.statkart.skif.service.sequence.IdServiceImpl;
import no.statkart.skif.service.sequence.SequenceBlockAllocatorService;
import no.statkart.skif.store.*;
import no.statkart.skif.store.module.common.RemoteServiceModuleStrategyWithServiceContextSVMapper;
import no.statkart.skif.store.relation.cache.RelationCacheProxyHandler;
import no.statkart.skif.store.relation.cache.StoreRelationCache;
import no.statkart.skif.store.service.LockService;
import no.statkart.skif.store.service.StoreService;
import no.statkart.skif.storetest.wsapi.StoreTestServiceContextMapper;
import no.statkart.skif.storetest.wsapi.exception.mapping.StoreTestExceptionMapper;
import no.statkart.skif.storetest.wsapi.exception.mapping.StoreTestExceptionMapping;
import no.statkart.skif.storetest.wsapi.mapping.StoreTestMapper;
import no.statkart.skif.storetest.wsapi.mapping.StoreTestMapping;

/**
 * Klientmodul for client-server tester som går mot StoreTestServer.
 *
 * @author Henrik Fredholm
 */
public class StoreTestClientModule extends SkifModule {
    public StoreTestClientModule(ModuleConfiguration moduleConfiguration) {
        super(moduleConfiguration);
    }

    @Override
    protected ModuleStrategyFactory defineDefaultModuleStrategyFactory() {
        return new ClientModuleStrategyFactory(RemoteServiceModuleStrategyWithServiceContextSVMapper.class);
    }

    @Override
    protected void configure() {
        final StoreTestMapping mapping = new StoreTestMapper(getProvider(SnapshotVersion.class)).getMapping();
        final StoreTestExceptionMapping exceptionMapping = new StoreTestExceptionMapper(mapping).getMapping();

        install(new RemoteServerModule(moduleConfiguration));
        install(new RunOnServerRemoteServiceModule(moduleConfiguration));
        install(new RemoteServiceModule(moduleConfiguration, new StoreTestGroup1Services().getServices(), mapping).setExceptionMapping(exceptionMapping));
        install(new RemoteServiceModule(moduleConfiguration, new StoreTestStoreServices().getServices(), mapping)
                .setExceptionMapping(exceptionMapping)
                .setServiceContextMapperClass(StoreTestServiceContextMapper.class)
        );
        install(new RemoteServiceModule(moduleConfiguration, new StoreTestStoreUpdateServices().getServices(), mapping)
                .setExceptionMapping(exceptionMapping)
                .setServiceContextMapperClass(StoreTestServiceContextMapper.class)
        );
        install(new RemoteServiceModule(moduleConfiguration, new StoreTestSequenceBlockAllocatorServices().getServices(), mapping)
                .setExceptionMapping(exceptionMapping)
                .setServiceContextMapperClass(StoreTestServiceContextMapper.class)
        );

        // DomainServiceFinder skal ha RelationCacheProxyHandler i CallServiceChain
        RemoteServiceModule domainServiceModule = new RemoteServiceModule(moduleConfiguration, new StoreTestDomainFinderServices().getServices(), mapping);
        domainServiceModule.getStrategy(ServiceMode.SINGLE_VM).getCallServiceChainFactorySpecification().getCallServiceChainProxyHandlers().add(0, RelationCacheProxyHandler.class);
        domainServiceModule.getStrategy(ServiceMode.JEE).getCallServiceChainFactorySpecification().getCallServiceChainProxyHandlers().add(0, RelationCacheProxyHandler.class);
        domainServiceModule.setExceptionMapping(exceptionMapping).setServiceContextMapperClass(StoreTestServiceContextMapper.class);

        install(domainServiceModule);

        bind(SequenceBlockAllocatorService.class).to(no.statkart.skif.storetest.service.id.SequenceBlockAllocatorService.class);
        bind(IdService.class).to(IdServiceImpl.class);
        bind(Store.class).to(StoreClient.class);

        bind(StoreService.class).to(no.statkart.skif.storetest.service.store.StoreService.class);
        bind(LockService.class).to(no.statkart.skif.storetest.service.lock.LockService.class);

        install(new RemoteServiceModule(moduleConfiguration, new StoreTestTestServices().getServices(), mapping)
                .setExceptionMapping(exceptionMapping)
                .setServiceContextMapperClass(StoreTestServiceContextMapper.class)
        );
        bind(no.statkart.skif.service.test.TestdataService.class).to(no.statkart.skif.storetest.service.test.TestdataService.class);

        //Legger til DBLockerService dersom man kjører i singleVm. Denne tjenesten finnes ikke som en webservice, og kan derfor ikke legges til ved kjøring av tester i client/server
        if (moduleConfiguration.getServiceMode() == ServiceMode.SINGLE_VM) {
            install(new RemoteServiceModule(moduleConfiguration, new StoreTestLocalServices().getServices(), mapping)); // Angir bare en mapping, siden det er irrelevant for en intern tjeneste
            bind(SkifUtil.typeLiteral(DBLockerService.class, Long.class)).to(no.statkart.skif.storetest.service.locker.DBLockerService.class);
            bind(SkifUtil.typeLiteral(DBLockerInTransactionService.class, Long.class)).to(no.statkart.skif.storetest.service.locker.DBLockerInTransactionService.class);
        }
    }

    @Provides
    StoreRelationCache provideStoreRelationCache(Store store) {
        return store.getRelationCache();
    }

    @Provides
    @Singleton
    StoreClient storeProvider(StoreService storeService, LockService lockService, Injector injector, SnapshotVersionContext snapshotVersionContext) {
        StoreSessionClient storeSession = createStoreSessionClient(storeService, lockService, snapshotVersionContext);
        StoreClient store = new StoreClient(storeSession, injector);
        injector.injectMembers(store);
        return store;
    }

    protected StoreSessionClient createStoreSessionClient(StoreService storeService, LockService lockService, SnapshotVersionContext snapshotVersionContext) {
        return new StoreSessionClient(storeService, lockService, snapshotVersionContext);
    }

}
