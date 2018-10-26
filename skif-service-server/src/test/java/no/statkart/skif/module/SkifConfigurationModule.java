package no.statkart.skif.module;

import no.statkart.skif.SkifModule;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.module.ModuleConfiguration;

/**
 * @author Henrik Fredholm
 */
public class SkifConfigurationModule extends SkifModule {
    public SkifConfigurationModule(ModuleConfiguration moduleConfiguration) {
        super(moduleConfiguration);
    }

    @Override
    protected void configure() {
        bind(ModuleConfiguration.class).toInstance(moduleConfiguration);
        bind(Configuration.class).toInstance(moduleConfiguration.getConfiguration());
    }
}
