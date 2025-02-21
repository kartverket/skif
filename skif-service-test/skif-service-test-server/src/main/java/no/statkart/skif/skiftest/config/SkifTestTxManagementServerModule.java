package no.statkart.skif.skiftest.config;

import no.statkart.skif.ServiceMode;
import no.statkart.skif.SkifModule;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.module.StrategyTuple;
import no.statkart.skif.service.chain.EJBServiceChainFactorySpecification;
import no.statkart.skif.service.ejb.EJBCounterProxyHandler;
import no.statkart.skif.service.ejb.EJBResourceProxyHandlerForConnection;
import no.statkart.skif.service.module.ServerModuleStrategyFactory;
import no.statkart.skif.service.module.server.ServerModule;
import no.statkart.skif.service.module.server.ServerServiceModule;
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
        // Konfigurer EJBServiceChain for denne modul til å bruke en factory som har en ProxyHandler for transaksjonshåndtering
        ModuleStrategyFactory factory = new ServerModuleStrategyFactory();
        StrategyTuple<ServerServiceModuleStrategy> prototype = factory.getPrototype(ServerServiceModule.class);
        prototype.getStrategy(ServiceMode.SINGLE_VM).setEjbServiceChainFactorySpecification(new EJBServiceChainFactorySpecification(EJBResourceProxyHandlerForConnection.class));
        prototype.getStrategy(ServiceMode.JEE).setEjbServiceChainFactorySpecification(new EJBServiceChainFactorySpecification(EJBResourceProxyHandlerForConnection.class));
        return factory;
    }

    @Override
    protected void configure() {
        install(new ServerModule(moduleConfiguration));

        // Eksempel: Legg på ekstra proxyhandler programmatisk for SkifTestTxManagementServices
        final ServerServiceModule serverServiceModule = new ServerServiceModule(moduleConfiguration, new SkifTestTxManagementServices().getServices());
        serverServiceModule
                .getStrategy(ServiceMode.SINGLE_VM)
                .getEjbServiceChainFactorySpecification()
                .appendEJBServiceChainProxyHandler(EJBCounterProxyHandler.class);
        install(serverServiceModule);

        install(new ResourceWithSingleConnectionModule(moduleConfiguration));
    }

}


