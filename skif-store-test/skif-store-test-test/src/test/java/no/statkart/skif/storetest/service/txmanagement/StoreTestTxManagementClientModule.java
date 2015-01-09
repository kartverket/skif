package no.statkart.skif.storetest.service.txmanagement;

import no.statkart.skif.SkifModule;
import no.statkart.skif.mapper.IdentityMapper;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.service.module.client.ClientModuleStrategyFactory;
import no.statkart.skif.service.module.common.RemoteServerModule;
import no.statkart.skif.service.module.common.RemoteServiceModule;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.module.common.RemoteServiceModuleStrategyWithServiceContextSVMapper;
import no.statkart.skif.storetest.config.StoreTestSequenceBlockAllocatorServices;
import no.statkart.skif.storetest.config.StoreTestTxManagementServices;
import no.statkart.skif.storetest.wsapi.exception.mapping.StoreTestExceptionMapper;
import no.statkart.skif.storetest.wsapi.exception.mapping.StoreTestExceptionMapping;
import no.statkart.skif.storetest.wsapi.mapping.StoreTestMapper;
import no.statkart.skif.storetest.wsapi.mapping.StoreTestMapping;

/**
 * @author Henrik Fredholm
 */
public class StoreTestTxManagementClientModule extends SkifModule {

        public StoreTestTxManagementClientModule(ModuleConfiguration moduleConfiguration) {
            super(moduleConfiguration);
        }

        @Override
        protected ModuleStrategyFactory defineDefaultModuleStrategyFactory() {
            return new ClientModuleStrategyFactory(RemoteServiceModuleStrategyWithServiceContextSVMapper.class);
        }

        @Override
        protected void configure() {
            StoreTestMapping mapping = new StoreTestMapper(getProvider(SnapshotVersion.class)).getMapping();
            StoreTestExceptionMapping exceptionMapping = new StoreTestExceptionMapper(mapping).getMapping();

            install(new RemoteServerModule(moduleConfiguration));
            install(new RemoteServiceModule(moduleConfiguration, new StoreTestTxManagementServices().getServices(), mapping).
                    setExceptionMapping(exceptionMapping));
            install(new RemoteServiceModule(moduleConfiguration, new StoreTestSequenceBlockAllocatorServices().getServices(), mapping).
                    setExceptionMapping(exceptionMapping));
        }
    }

