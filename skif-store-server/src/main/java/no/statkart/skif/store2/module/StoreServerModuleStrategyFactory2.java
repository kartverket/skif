package no.statkart.skif.store2.module;

import no.statkart.skif.ServiceMode;
import no.statkart.skif.module.StrategyTuple;
import no.statkart.skif.service.module.ServerModuleStrategyFactory;
import no.statkart.skif.store2.module.server.ServerStoreModule2;
import no.statkart.skif.store2.module.server.ServerStoreModuleStrategy2;

/**
 * @author Henrik Fredholm
 */
public class StoreServerModuleStrategyFactory2 extends ServerModuleStrategyFactory {
    public StoreServerModuleStrategyFactory2() {
        addStrategyForServerStoreModule();
    }

    protected void addStrategyForServerStoreModule() {
        // ServerStoreModuleStrategy bruker samme klass for JEE og SingleVM mode.
        final StrategyTuple<ServerStoreModuleStrategy2> strategyTuple = addPrototype(
                ServerStoreModule2.class,
                new StrategyTuple<ServerStoreModuleStrategy2>(ServerStoreModuleStrategy2.class, ServerStoreModuleStrategy2.class));
        strategyTuple.getStrategy(ServiceMode.JEE).setHibernateConfigurationFilename("hibernate-server.properties");
        strategyTuple.getStrategy(ServiceMode.SINGLE_VM).setHibernateConfigurationFilename("hibernate-singlevm.properties");
        addPrototype(ServerStoreModule2.class, strategyTuple);
    }
}

