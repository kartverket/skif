package no.statkart.skif.skiftest.service.txmanagement;

import no.statkart.skif.SkifModule;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.service.module.client.ClientModuleStrategyFactory;
import no.statkart.skif.service.module.common.RemoteServerModule;
import no.statkart.skif.service.module.common.RemoteServiceModule;
import no.statkart.skif.skiftest.config.SkifTestTxManagementServices;
import no.statkart.skif.skiftest.wsapi.exception.impl.mapping.SkifTestExceptionMapper;
import no.statkart.skif.skiftest.wsapi.mapping.SkifTestMapper;

/**
 * @author Henrik Fredholm
 */
public class SkifTestTxManagementClientModule extends SkifModule {

        public SkifTestTxManagementClientModule(ModuleConfiguration moduleConfiguration) {
            super(moduleConfiguration);
        }

        @Override
        protected ModuleStrategyFactory defineDefaultModuleStrategyFactory(ModuleConfiguration moduleConfiguration) {
            return new ClientModuleStrategyFactory();
        }

        @Override
        protected void configure() {
            install(new RemoteServerModule(moduleConfiguration));
            install(new RemoteServiceModule(moduleConfiguration, new SkifTestTxManagementServices().getServices(), new SkifTestMapper().getMapping()).
                    setExceptionMapping(new SkifTestExceptionMapper().getMapping()));
        }
    }

