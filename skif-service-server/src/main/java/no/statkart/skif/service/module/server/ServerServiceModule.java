package no.statkart.skif.service.module.server;

import no.statkart.skif.module.ModuleWithStrategy;
import no.statkart.skif.module.ModuleConfiguration;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class ServerServiceModule extends ModuleWithStrategy<ServerServiceModuleStrategy> {
    protected final Set<Class<? extends Object>> services = new HashSet<Class<? extends Object>>();

    public ServerServiceModule(ModuleConfiguration configuration, Collection<Class<? extends Object>> services) {
        super(ServerServiceModuleStrategy.class, configuration);
        this.services.addAll(services);
    }

    @Override
    protected void configure() {
        setStrategyInstance();
        requireBindings();
        configureServices();
    }

    protected void requireBindings() {
    }


    private void configureServices() {
        for (Class<? extends Object> service : services) {
            strategy.bindServiceChainFactoriesForService(binder(), service);
            strategy.bindService(binder(), service);
        }
    }
}
