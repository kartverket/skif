package no.statkart.skif.service.module;

import no.statkart.skif.module.AbstractModuleStrategyFactory;
import no.statkart.skif.module.StrategyTuple;
import no.statkart.skif.module.StrategyTuples;
import no.statkart.skif.service.module.common.RemoteServiceModule;
import no.statkart.skif.service.module.common.RemoteServiceModuleStrategy;
import no.statkart.skif.service.module.server.*;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public class ServerModuleStrategyFactory extends AbstractModuleStrategyFactory {
    public ServerModuleStrategyFactory() {
        addStrategyForServerModule();
        addStrategyForRemoteServiceModule();
        addStrategyForImplementationServiceModule();
        addStrategyForWSServerServiceModule();
    }

    protected void addStrategyForServerModule() {
        final StrategyTuple<ServerModuleStrategy> strategyTuple = addPrototype(
                ServerModule.class,
                StrategyTuples.newStrategyTuple(ServerModuleStrategy.class));
        addPrototype(ServerModule.class, strategyTuple);
    }

    protected void addStrategyForRemoteServiceModule() {
        final StrategyTuple<RemoteServiceModuleStrategy> strategyTuple = addPrototype(
                RemoteServiceModule.class,
                StrategyTuples.newStrategyTuple(RemoteServiceModuleStrategy.class));
        addPrototype(RemoteServiceModule.class, strategyTuple);
    }

    protected void addStrategyForImplementationServiceModule() {
        final StrategyTuple<ServerServiceModuleStrategy> strategyTuple = addPrototype(
                ServerServiceModule.class,
                StrategyTuples.newStrategyTuple(ServerServiceModuleStrategy.class));
        addPrototype(ServerServiceModule.class, strategyTuple);
    }

    protected void addStrategyForWSServerServiceModule() {
        final StrategyTuple<WSServerServiceModuleStrategy> strategyTuple = addPrototype(
                WSServerServiceModule.class,
                StrategyTuples.newStrategyTuple(WSServerServiceModuleStrategy.class));
        addPrototype(WSServerServiceModule.class, strategyTuple);
    }
}
