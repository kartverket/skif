package no.statkart.skif.skiftest.service.test;

import com.google.inject.name.Names;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.SkifModule;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.service.CallIdProvider;
import no.statkart.skif.service.annotation.CallId;
import no.statkart.skif.service.chain.CallServiceChainFactorySpecification;
import no.statkart.skif.service.chain.ClientCallServiceChainFactoryJEE;
import no.statkart.skif.service.logging.ClientCallLogger;
import no.statkart.skif.service.logging.DefaultClientCallLogger;
import no.statkart.skif.service.module.client.ClientModuleStrategyFactory;
import no.statkart.skif.service.module.common.RemoteServerModule;
import no.statkart.skif.service.module.common.RemoteServiceModule;
import no.statkart.skif.service.module.common.RunOnServerRemoteServiceModule;
import no.statkart.skif.service.proxy.ClientLoggingProxyHandler;
import no.statkart.skif.skiftest.config.SkifTestGroup1Services;
import no.statkart.skif.skiftest.config.SkifTestGroupABCDServices;
import no.statkart.skif.skiftest.config.SkifTestGroupExServices;
import no.statkart.skif.skiftest.service.SkifTestServiceContext;
import no.statkart.skif.skiftest.wsapi.SkifTestServiceContextMapper;
import no.statkart.skif.skiftest.wsapi.exception.impl.mapping.SkifTestExceptionMapper;
import no.statkart.skif.skiftest.wsapi.exception.simple.mapping.SkifTestSimpleExceptionMapper;
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
            bind(String.class).annotatedWith(Names.named("client")).toInstance("ClientString");

            bind(Long.class).annotatedWith(CallId.class).toProvider(CallIdProvider.class);
            bind(ClientCallLogger.class).to(DefaultClientCallLogger.class);

            install(new RemoteServerModule(moduleConfiguration).setServiceContextClass(SkifTestServiceContext.class));
            install(new RunOnServerRemoteServiceModule(moduleConfiguration));
            install(new RemoteServiceModule(moduleConfiguration, new SkifTestGroupExServices().getServices(), new SkifTestMapper().getMapping()).
                    setExceptionMapping(new SkifTestSimpleExceptionMapper().getMapping()));
            RemoteServiceModule module = new RemoteServiceModule(moduleConfiguration, new SkifTestGroupABCDServices().getServices(), new SkifTestMapper().getMapping()).
                    setExceptionMapping(new SkifTestExceptionMapper().getMapping());
            // Kun for JEE mode
            module.getStrategy(ServiceMode.JEE).setCallServiceChainFactorySpecification(new CallServiceChainFactorySpecification(ClientCallServiceChainFactoryJEE.class, ClientLoggingProxyHandler.class));
            install(module);
            install(new RemoteServiceModule(moduleConfiguration, new SkifTestGroup1Services().getServices(), new SkifTestMapper().getMapping()).setServiceContextMapperClass(SkifTestServiceContextMapper.class));
        }
    }

