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
 * En ModuleStrategyFactory for klienter. Definerer strategier for :
 * <ul>
 *     <li>RemoteServerModule</li>
 *     <li>RemoteServiceModule</li>
 * </ul>
 *
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @since 2.0
 */
public class ClientModuleStrategyFactory extends AbstractModuleStrategyFactory {
    final Class<? extends RemoteServiceModuleStrategy> remoteServiceModuleStrategyBaseClass;

    /**
     * Oppretter en factory for klienten som anvender standard innstillinger for SKIF service rammeverk
     */
    public ClientModuleStrategyFactory() {
        this(RemoteServiceModuleStrategy.class);
    }

    /**
     * Oppretter en factory for klienten som anvender {@code remoteServiceModuleStrategyClass}  for konfigurasjon av
     * services.
     */
    public ClientModuleStrategyFactory(Class<? extends RemoteServiceModuleStrategy> remoteServiceModuleStrategyClass) {
        this.remoteServiceModuleStrategyBaseClass = remoteServiceModuleStrategyClass;
        addStrategyForRemoteServiceModule();
        addStrategyForRemoteServerModule();
    }

    protected void addStrategyForRemoteServiceModule() {
        addPrototype(
                RemoteServiceModule.class,
                StrategyTuples.newStrategyTuple(remoteServiceModuleStrategyBaseClass));
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
