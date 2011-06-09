package no.statkart.skif.service.module.server;

import no.statkart.skif.ServiceMode;
import no.statkart.skif.SkifModule;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.guava.Preconditions;
import no.statkart.skif.module.ModuleConfiguration;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public class WSServerModule extends SkifModule {
    public WSServerModule(ModuleConfiguration moduleConfiguration) {
        super(moduleConfiguration);
    }

    public WSServerModule(Configuration configuration) {
        super(configuration);
    }
    @Override
    protected void configure() {
        Preconditions.checkArgument(moduleConfiguration.getServiceMode()== ServiceMode.JEE, "Kun ServiceMode.JEE er støttet");
    }
}
