package no.statkart.skif.service.module;

import no.statkart.skif.module.AbstractModuleStrategyFactory;
import no.statkart.skif.module.StrategyTuple;
import no.statkart.skif.module.StrategyTuples;
import no.statkart.skif.service.module.common.RemoteServiceModule;
import no.statkart.skif.service.module.common.RemoteServiceModuleStrategy;
import no.statkart.skif.service.module.server.*;

/**
 * En ModuleStrategyFactory for server. Definerer strategier for :
 * <ul>
 *     <li>ServerModule</li>
 *     <li>RemoteServiceModule</li>
 *     <li>ImplementationServiceModule</li>
 *     <li>WSServerServiceModule</li>
 * </ul> * @author Henrik Fredholm
 * @since 2.0
 */
public class ServerModuleStrategyFactory extends AbstractModuleStrategyFactory {
    final Class<? extends RemoteServiceModuleStrategy> remoteServiceModuleStrategyBaseClass;

    /**
     * Oppretter en factory for server som anvender standard innstillinger for SKIF service rammeverk
     */
    public ServerModuleStrategyFactory() {
        this(RemoteServiceModuleStrategy.class);
    }

    /**
     * Oppretter en factory for server som anvender {@code remoteServiceModuleStrategyClass} for konfigurasjon av
     * services mot en annen server.
     */
    public ServerModuleStrategyFactory(Class<? extends RemoteServiceModuleStrategy> remoteServiceModuleStrategyClass) {
        this.remoteServiceModuleStrategyBaseClass = remoteServiceModuleStrategyClass;
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
                StrategyTuples.newStrategyTuple(remoteServiceModuleStrategyBaseClass));
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
