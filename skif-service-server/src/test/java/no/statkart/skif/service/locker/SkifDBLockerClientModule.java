package no.statkart.skif.service.locker;

import no.statkart.skif.SkifModule;
import no.statkart.skif.config.SkifServices;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.service.module.ClientModuleStrategyFactory;
import no.statkart.skif.service.module.common.RemoteServerModule;
import no.statkart.skif.service.module.common.RemoteServiceModule;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class SkifDBLockerClientModule extends SkifModule {

    public SkifDBLockerClientModule(ModuleConfiguration moduleConfiguration) {
        super(moduleConfiguration);
    }

    @Override
    protected ModuleStrategyFactory defineDefaultModuleStrategyFactory() {
        return new ClientModuleStrategyFactory();
    }


    @Override
    protected void configure() {
        install(new RemoteServerModule(moduleConfiguration));
        install(new RemoteServiceModule(moduleConfiguration, new SkifServices().getServices(), new SkifMapper().getMapping()));
    }
}
