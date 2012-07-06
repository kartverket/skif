package no.statkart.skif.service.module.client;

import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.service.module.common.RemoteServerModule;
import no.statkart.skif.service.module.common.RunOnServerRemoteServiceModule;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class RunOnRemoteServerClientModule extends ClientModule {
    public RunOnRemoteServerClientModule(ModuleConfiguration moduleConfiguration) {
        super(moduleConfiguration);
    }

    @Override
    protected void configure() {
        install(new RemoteServerModule(moduleConfiguration));
        install(new RunOnServerRemoteServiceModule(moduleConfiguration));
    }
}
