package no.statkart.skif.storetest.util.testsupport2;

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
import no.statkart.skif.store2.*;
import no.statkart.skif.storetest.config2.StoreTestGroup1Services2;
import no.statkart.skif.storetest.config2.StoreTestServerModule2;
import no.statkart.skif.storetest.config2.StoreTestStoreServices2;
import no.statkart.skif.storetest.service2.store2.StoreReadChainClient2;
import no.statkart.skif.storetest.wsapi.StoreTestServiceContextMapper2;
import no.statkart.skif.storetest.wsapi.exception.simple.mapping.StoreTestExceptionMapper2;
import no.statkart.skif.storetest.wsapi.mapping2.StoreTestMapper2;
import no.statkart.skif.util.testsupport.SkifTestCase;

/**
 * @author Henrik Fredholm
 */
public class StoreTestTestCase2 extends SkifTestCase {
    public StoreTestTestCase2() {
        setModuleClass(ClientModule.class);
        setSingleVmServerModuleClass(StoreTestServerModule2.class);
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
            install(new RemoteServiceModule(moduleConfiguration, new StoreTestGroup1Services2().getServices(), new StoreTestMapper2().getMapping()).setExceptionMapping(new StoreTestExceptionMapper2().getMapping()));
            install(new RemoteServiceModule(moduleConfiguration, new StoreTestStoreServices2().getServices(), new StoreTestMapper2().getMapping())
                    .setExceptionMapping(new StoreTestExceptionMapper2().getMapping())
                    .setServiceContextMapperClass(StoreTestServiceContextMapper2.class)
            );
            install(new RemoteServiceModule(moduleConfiguration, new SkifServices().getServices(), new SkifMapper().getMapping()));
            bind(StoreReadChain2.class).to(StoreReadChainClient2.class);
        }

        @Provides
        @Singleton
        Store2 storeProvider(StoreReadChain2 storeReadChain, Injector injector) {
            StoreCache2 storeCache = new StoreCache2();
            StoreSessionChain2[] storeChainList = {
                    new StoreSessionReadClient2(storeReadChain)
            };
            StoreClient2 store = new StoreClient2(storeCache, storeChainList);
            injector.injectMembers(store);
            store.init();
            return store;
        }


    }
}
