package no.statkart.skif.store.module;

import no.statkart.skif.ServiceMode;
import no.statkart.skif.module.StrategyTuple;
import no.statkart.skif.service.module.ServerModuleStrategyFactory;
import no.statkart.skif.service.module.common.RemoteServiceModuleStrategy;
import no.statkart.skif.store.module.server.ServerStoreModule;
import no.statkart.skif.store.module.server.ServerStoreModuleStrategy;

/**
 * @author Henrik Fredholm
 */
public class StoreServerModuleStrategyFactory extends ServerModuleStrategyFactory {
    public StoreServerModuleStrategyFactory() {
        addStrategyForServerStoreModule();
    }
    public StoreServerModuleStrategyFactory(Class<? extends RemoteServiceModuleStrategy> remoteServiceModuleStrategyBaseClass) {
        super(remoteServiceModuleStrategyBaseClass);
    }

    protected void addStrategyForServerStoreModule() {
        // ServerStoreModuleStrategy bruker samme klass for JEE og SingleVM mode.
        final StrategyTuple<ServerStoreModuleStrategy> strategyTuple = addPrototype(
                ServerStoreModule.class,
                new StrategyTuple<ServerStoreModuleStrategy>(ServerStoreModuleStrategy.class, ServerStoreModuleStrategy.class));
        strategyTuple.getStrategy(ServiceMode.JEE).setHibernateConfigurationFilename("hibernate-server.properties");
        strategyTuple.getStrategy(ServiceMode.SINGLE_VM).setHibernateConfigurationFilename("hibernate-singlevm.properties");
        addPrototype(ServerStoreModule.class, strategyTuple);
    }
}

