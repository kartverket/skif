package no.statkart.skif.store.module;

import no.statkart.skif.ServiceMode;
import no.statkart.skif.module.StrategyTuple;
import no.statkart.skif.service.module.ServerModuleStrategyFactory;
import no.statkart.skif.store.module.server.NonhistServerStoreModule;
import no.statkart.skif.store.module.server.ServerStoreModule;
import no.statkart.skif.store.module.server.ServerStoreModuleStrategy;

/**
 * @author Henrik Fredholm
 */
public class NonhistStoreServerModuleStrategyFactory extends ServerModuleStrategyFactory {
    public NonhistStoreServerModuleStrategyFactory() {
        addStrategyForServerStoreModule();
    }

    protected void addStrategyForServerStoreModule() {
        // ServerStoreModuleStrategy bruker samme klass for JEE og SingleVM mode.
        final StrategyTuple<ServerStoreModuleStrategy> strategyTuple = addPrototype(
                NonhistServerStoreModule.class,
                new StrategyTuple<ServerStoreModuleStrategy>(ServerStoreModuleStrategy.class, ServerStoreModuleStrategy.class));
        strategyTuple.getStrategy(ServiceMode.JEE).setHibernateConfigurationFilename("hibernate-server.properties");
        strategyTuple.getStrategy(ServiceMode.SINGLE_VM).setHibernateConfigurationFilename("hibernate-singlevm.properties");
        addPrototype(NonhistServerStoreModule.class, strategyTuple);
    }
}

