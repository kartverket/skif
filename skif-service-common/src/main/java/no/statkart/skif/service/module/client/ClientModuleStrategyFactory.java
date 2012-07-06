package no.statkart.skif.service.module.client;

import no.statkart.skif.module.AbstractModuleStrategyFactory;
import no.statkart.skif.module.StrategyTuples;
import no.statkart.skif.service.module.common.RemoteServerModule;
import no.statkart.skif.service.module.common.RemoteServerModuleStrategy;
import no.statkart.skif.service.module.common.RemoteServiceModule;
import no.statkart.skif.service.module.common.RemoteServiceModuleStrategy;
import no.statkart.skif.service.module.common.RemoteWSServiceModule;
import no.statkart.skif.service.module.common.RemoteWSServiceModuleStrategy;

/**
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @since 2.0
 */
public class ClientModuleStrategyFactory extends AbstractModuleStrategyFactory {
    public ClientModuleStrategyFactory() {
        addStrategyForRemoteServiceModule();
        addStrategyForRemoteServerModule();
    }

    protected void addStrategyForRemoteServiceModule() {
        addPrototype(
                RemoteServiceModule.class,
                StrategyTuples.newStrategyTuple(RemoteServiceModuleStrategy.class));
        addPrototype(
                RemoteWSServiceModule.class,
                StrategyTuples.newStrategyTuple(RemoteWSServiceModuleStrategy.class)
        );
    }

    protected void addStrategyForRemoteServerModule() {
        addPrototype(
                RemoteServerModule.class,
                StrategyTuples.newStrategyTuple(RemoteServerModuleStrategy.class));
    }
}
