package no.statkart.skif.service.module;

import no.statkart.skif.module.AbstractModuleStrategyFactory;
import no.statkart.skif.module.StrategyTuple;
import no.statkart.skif.module.StrategyTuples;
import no.statkart.skif.service.module.common.RemoteServerModule;
import no.statkart.skif.service.module.common.RemoteServerModuleStrategy;
import no.statkart.skif.service.module.common.RemoteServiceModule;
import no.statkart.skif.service.module.common.RemoteServiceModuleStrategy;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public class ClientModuleStrategyFactory extends AbstractModuleStrategyFactory {
    public ClientModuleStrategyFactory() {
        addStrategyForRemoteServiceModule();
        addStrategyForRemoteServerModule();
    }

    protected void addStrategyForRemoteServiceModule() {
        final StrategyTuple<RemoteServiceModuleStrategy> prototype = addPrototype(
                RemoteServiceModule.class,
                StrategyTuples.newStrategyTuple(RemoteServiceModuleStrategy.class));
    }

    protected void addStrategyForRemoteServerModule() {
        addPrototype(
                RemoteServerModule.class,
                StrategyTuples.newStrategyTuple(RemoteServerModuleStrategy.class));
    }
}
