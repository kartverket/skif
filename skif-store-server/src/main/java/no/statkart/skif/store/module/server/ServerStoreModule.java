package no.statkart.skif.store.module.server;

import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleWithStrategy;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class ServerStoreModule extends ModuleWithStrategy<ServerStoreModuleStrategy> {

    public ServerStoreModule(Class<ServerStoreModuleStrategy> strategyClass, ModuleConfiguration moduleConfiguration) {
        super(strategyClass, moduleConfiguration);
    }

    @Override
    protected void configure() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
