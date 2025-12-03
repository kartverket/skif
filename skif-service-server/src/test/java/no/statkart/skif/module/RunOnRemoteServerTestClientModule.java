package no.statkart.skif.module;

import no.statkart.skif.SkifModule;
import no.statkart.skif.service.module.client.ClientModuleStrategyFactory;
import no.statkart.skif.service.module.common.RemoteServerModule;
import no.statkart.skif.service.module.common.RunOnServerRemoteServiceModule;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class RunOnRemoteServerTestClientModule extends SkifModule {
    public RunOnRemoteServerTestClientModule(ModuleConfiguration moduleConfiguration) {
        super(moduleConfiguration);
    }

    @Override
    protected ModuleStrategyFactory defineDefaultModuleStrategyFactory(ModuleConfiguration moduleConfiguration) {
        return new ClientModuleStrategyFactory();
    }

    @Override
    protected void configure() {
        install(new RemoteServerModule(moduleConfiguration));
        install(new RunOnServerRemoteServiceModule(moduleConfiguration));
    }
}
