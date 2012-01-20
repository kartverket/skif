package no.statkart.skif.skiftest.config;

import no.statkart.skif.ServiceMode;
import no.statkart.skif.SkifModule;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.module.StrategyTuple;
import no.statkart.skif.service.chain.EJBServiceChainFactoryWithTxSpecification;
import no.statkart.skif.service.ejb.EJBResourceProxyHandlerForConnection;
import no.statkart.skif.service.module.ServerModuleStrategyFactory;
import no.statkart.skif.service.module.server.ResourceWithSingleConnectionModule;
import no.statkart.skif.service.module.server.ServerServiceModule;
import no.statkart.skif.service.module.server.ServerModule;
import no.statkart.skif.service.module.server.ServerServiceModuleStrategy;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class SkifTestTxManagementServerModule extends SkifModule {
    public SkifTestTxManagementServerModule(ModuleConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected ModuleStrategyFactory defineDefaultModuleStrategyFactory() {
        // Konfigurer EJBServiceChain til å bruke en factory som har en ProxyHandler for transaksjonshåndtering
        ModuleStrategyFactory factory = new ServerModuleStrategyFactory();
        StrategyTuple<ServerServiceModuleStrategy> prototype = factory.getPrototype(ServerServiceModule.class);
        prototype.getStrategy(ServiceMode.SINGLE_VM).setEjbServiceChainFactorySpecification(new EJBServiceChainFactoryWithTxSpecification(EJBResourceProxyHandlerForConnection.class));
        prototype.getStrategy(ServiceMode.JEE).setEjbServiceChainFactorySpecification(new EJBServiceChainFactoryWithTxSpecification(EJBResourceProxyHandlerForConnection.class));
        return factory;
    }

    @Override
    protected void configure() {
        install(new ServerModule(moduleConfiguration));

        install(new ServerServiceModule(moduleConfiguration, new SkifTestTxManagementServices().getServices()));
        install(new ServerServiceModule(moduleConfiguration, new SkifTestSequenceBlockAllocatorServices().getServices()));

        install(new ResourceWithSingleConnectionModule(moduleConfiguration));
    }

}


