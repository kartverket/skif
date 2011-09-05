package no.statkart.skif.storetest.service.txmanagement;

import no.statkart.skif.SkifModule;
import no.statkart.skif.mapper.IdentityMapper;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.service.module.ClientModuleStrategyFactory;
import no.statkart.skif.service.module.common.RemoteServerModule;
import no.statkart.skif.service.module.common.RemoteServiceModule;
import no.statkart.skif.storetest.config.StoreTestSequenceBlockAllocatorServices;
import no.statkart.skif.storetest.config.StoreTestTxManagementServices;
import no.statkart.skif.storetest.wsapi.exception.impl.mapping.StoreTestExceptionMapper;

/**
 * @author Henrik Fredholm
 */
public class StoreTestTxManagementClientModule extends SkifModule {

        public StoreTestTxManagementClientModule(ModuleConfiguration moduleConfiguration) {
            super(moduleConfiguration);
        }

        @Override
        protected ModuleStrategyFactory defineDefaultModuleStrategyFactory() {
            return new ClientModuleStrategyFactory();
        }

        @Override
        protected void configure() {
            install(new RemoteServerModule(moduleConfiguration));
            install(new RemoteServiceModule(moduleConfiguration, new StoreTestTxManagementServices().getServices(), new IdentityMapper().getMapping()).
                    setExceptionMapping(new StoreTestExceptionMapper().getMapping()));
            install(new RemoteServiceModule(moduleConfiguration, new StoreTestSequenceBlockAllocatorServices().getServices(), new IdentityMapper().getMapping()).
                    setExceptionMapping(new StoreTestExceptionMapper().getMapping()));
        }
    }

