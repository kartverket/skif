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
import no.statkart.skif.store.Store;
import no.statkart.skif.store.StoreClient;
import no.statkart.skif.store.StoreSessionClient;
import no.statkart.skif.store.service.StoreService;
import no.statkart.skif.storetest.wsapi.StoreTestServiceContextMapper;
import no.statkart.skif.storetest.wsapi.exception.mapping.StoreTestExceptionMapper;
import no.statkart.skif.storetest.wsapi.exception.mapping.StoreTestExceptionMapping;
import no.statkart.skif.storetest.wsapi.mapping.StoreTestMapper;
import no.statkart.skif.storetest.wsapi.mapping.StoreTestMapping;

/**
 * Klientmodule for client-server tester som går mot StoreTestServer.
 *
 * @author Henrik Fredholm
 */
public class StoreTestClientModule extends SkifModule {
    public StoreTestClientModule(ModuleConfiguration moduleConfiguration) {
        super(moduleConfiguration);
    }

    @Override
    protected ModuleStrategyFactory defineDefaultModuleStrategyFactory() {
        return new ClientModuleStrategyFactory();
    }

    @Override
    protected void configure() {
        final StoreTestMapping mapping = new StoreTestMapper().getMapping();
        final StoreTestExceptionMapping exceptionMapping = new StoreTestExceptionMapper().getMapping();

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
        bind(SequenceBlockAllocatorService.class).to(no.statkart.skif.storetest.service.id.SequenceBlockAllocatorService.class);
        bind(IdService.class).to(IdServiceImpl.class);

        bind(StoreService.class).to(no.statkart.skif.storetest.service.store.StoreService.class);

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

    /*
        @Provides
        @Singleton
        Store storeProvider(StoreReadChain storeReadChain, Injector injector) {
            StoreCache storeCache = new StoreCache();
            StoreSessionChain[] storeChainList = {
                    new StoreSessionReadClient(storeReadChain)
            };
            StoreClient store = new StoreClient(storeCache, storeChainList);
            injector.injectMembers(store);
            store.init();
            return store;
        }
    */

    @Provides
    @Singleton
    Store storeProvider(StoreService storeService, Injector injector) {
        StoreSessionClient storeSession = new StoreSessionClient(storeService);
        Store store = new StoreClient(storeSession, injector);
        injector.injectMembers(store);
        return store;
    }

}
