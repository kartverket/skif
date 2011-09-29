package no.statkart.skif.storetest.util.testsupport;

import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import no.statkart.skif.SkifModule;
import no.statkart.skif.config.SkifServices;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.service.locker.SkifMapper;
import no.statkart.skif.service.module.ClientModuleStrategyFactory;
import no.statkart.skif.service.module.common.RemoteServerModule;
import no.statkart.skif.service.module.common.RemoteServiceModule;
import no.statkart.skif.store.*;
import no.statkart.skif.storetest.config.StoreTestGroup1Services;
import no.statkart.skif.storetest.config.StoreTestServerModule;
import no.statkart.skif.storetest.config.StoreTestStoreServices;
import no.statkart.skif.storetest.service.store.StoreReadChainClient;
import no.statkart.skif.storetest.wsapi.StoreTestServiceContextMapper;
import no.statkart.skif.storetest.wsapi.exception.simple.mapping.StoreTestExceptionMapper2;
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
            install(new RemoteServiceModule(moduleConfiguration, new StoreTestGroup1Services().getServices(), new StoreTestMapper().getMapping()).setExceptionMapping(new StoreTestExceptionMapper2().getMapping()));
            install(new RemoteServiceModule(moduleConfiguration, new StoreTestStoreServices().getServices(), new StoreTestMapper().getMapping())
                    .setExceptionMapping(new StoreTestExceptionMapper2().getMapping())
                    .setServiceContextMapperClass(StoreTestServiceContextMapper.class)
            );
            install(new RemoteServiceModule(moduleConfiguration, new SkifServices().getServices(), new SkifMapper().getMapping()));
            bind(StoreReadChain.class).to(StoreReadChainClient.class);
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
