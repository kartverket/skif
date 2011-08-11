package no.statkart.skif.skiftest.service.test;

import no.statkart.skif.SkifModule;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.service.module.ClientModuleStrategyFactory;
import no.statkart.skif.service.module.common.RemoteServerModule;
import no.statkart.skif.service.module.common.RemoteServiceModule;
import no.statkart.skif.skiftest.config.SkifTestGroupABCDServices;
import no.statkart.skif.skiftest.config.SkifTestGroupExServices;
import no.statkart.skif.skiftest.wsapi.exception.impl.mapping.SkifTestExceptionMapper;
import no.statkart.skif.skiftest.wsapi.exception.simple.mapping.SkifTestExceptionMapper2;
import no.statkart.skif.skiftest.wsapi.mapping.SkifTestMapper;

/**
 * @author Henrik Fredholm
 */
public class SkifTestClientModule extends SkifModule {

        public SkifTestClientModule(ModuleConfiguration moduleConfiguration) {
            super(moduleConfiguration);
        }

        @Override
        protected ModuleStrategyFactory defineDefaultModuleStrategyFactory() {
            return new ClientModuleStrategyFactory();
        }

        @Override
        protected void configure() {
            install(new RemoteServerModule(moduleConfiguration));
            install(new RemoteServiceModule(moduleConfiguration, new SkifTestGroupExServices().getServices(), new SkifTestMapper().getMapping()).
                    setExceptionMapping(new SkifTestExceptionMapper2().getMapping()));
            install(new RemoteServiceModule(moduleConfiguration, new SkifTestGroupABCDServices().getServices(), new SkifTestMapper().getMapping()).
                    setExceptionMapping(new SkifTestExceptionMapper().getMapping()));

        }
    }

