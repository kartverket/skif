package no.statkart.skif.storetest.util.testsupport;

import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.SkifModule;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.service.locker.DBLockerService;
import no.statkart.skif.service.module.ClientModuleStrategyFactory;
import no.statkart.skif.service.module.common.RemoteServerModule;
import no.statkart.skif.service.module.common.RemoteServiceModule;
import no.statkart.skif.service.module.server.ServerServiceModule;
import no.statkart.skif.store.*;
import no.statkart.skif.storetest.config.StoreTestGroup1Services;
import no.statkart.skif.storetest.config.StoreTestLocalServices;
import no.statkart.skif.storetest.config.StoreTestServerModule;
import no.statkart.skif.storetest.config.StoreTestStoreServices;
import no.statkart.skif.storetest.wsapi.StoreTestServiceContextMapper;
import no.statkart.skif.storetest.wsapi.exception.mapping.StoreTestExceptionMapper;
import no.statkart.skif.storetest.wsapi.mapping.StoreTestMapper;
import no.statkart.skif.util.testsupport.SkifTestCase;

/**
 * @author Henrik Fredholm
 */
public class StoreTestTestCase extends SkifTestCase {
    public StoreTestTestCase() {
        setModuleClass(ClientModule.class);
        setSingleVmServerModuleClass(StoreTestServerModule.class);
    }

    public static class ClientModule extends SkifModule {

        public ClientModule(ModuleConfiguration moduleConfiguration) {
            super(moduleConfiguration);
        }

        @Override
        protected ModuleStrategyFactory defineDefaultModuleStrategyFactory() {
            return new ClientModuleStrategyFactory();
        }

        @Override
        protected void configure() {
            install(new RemoteServerModule(moduleConfiguration));
            install(new RemoteServiceModule(moduleConfiguration, new StoreTestGroup1Services().getServices(), new StoreTestMapper().getMapping()).setExceptionMapping(new StoreTestExceptionMapper().getMapping()));
            install(new RemoteServiceModule(moduleConfiguration, new StoreTestStoreServices().getServices(), new StoreTestMapper().getMapping())
                    .setExceptionMapping(new StoreTestExceptionMapper().getMapping())
                    .setServiceContextMapperClass(StoreTestServiceContextMapper.class)
            );
            //Legger til DBLockerService dersom man kjører i singleVm. Denne tjenesten finnes ikke som en webservice, og kan derfor ikke legges til ved kjøring av tester i client/server
            if(moduleConfiguration.getServiceMode() == ServiceMode.SINGLE_VM) {
                install(new RemoteServiceModule(moduleConfiguration, new StoreTestLocalServices().getServices(), new StoreTestMapper().getMapping())); // Angir bare en mapping, siden det er irrelevant for en intern tjeneste
                bind(DBLockerService.class).to(no.statkart.skif.storetest.service.locker.DBLockerService.class);
            }
            bind(StoreReadChain.class).to(StoreReadChainClient.class);
            bind(StoreService.class).to(no.statkart.skif.storetest.service.store.StoreService.class);
        }

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


    }
}
