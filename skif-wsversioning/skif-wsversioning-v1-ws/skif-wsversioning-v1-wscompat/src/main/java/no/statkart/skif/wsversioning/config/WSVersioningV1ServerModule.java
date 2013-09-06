package no.statkart.skif.wsversioning.config;

import no.statkart.skif.SkifModule;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.service.module.server.ServerServiceModule;

/**
 * Modul for V1-API-et. Denne modulen kan anta at {@link WSVersioningServerModule} er innstallert.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class WSVersioningV1ServerModule extends SkifModule {
    public WSVersioningV1ServerModule(ModuleConfiguration moduleConfiguration) {
        super(moduleConfiguration);
    }

    @Override
    protected void configure() {
        install(new ServerServiceModule(moduleConfiguration, new WSVersioningCompatServicesV1().getServices()));
    }
}
