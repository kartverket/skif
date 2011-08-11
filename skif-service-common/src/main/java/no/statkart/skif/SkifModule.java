package no.statkart.skif;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.module.DefaultModuleConfiguration;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleStrategyFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public abstract class SkifModule extends AbstractModule {
    protected final ModuleConfiguration moduleConfiguration;

    public SkifModule(ModuleConfiguration moduleConfiguration) {
        this.moduleConfiguration = addDefaultFactory(moduleConfiguration);
    }

    public SkifModule(Configuration configuration) {
        this.moduleConfiguration = addDefaultFactory(new DefaultModuleConfiguration(configuration));
    }

    public ServiceMode getServiceMode() {
        return moduleConfiguration.getServiceMode();
    }

    protected final ModuleConfiguration addDefaultFactory(ModuleConfiguration moduleConfiguration) {
        ModuleConfiguration c = moduleConfiguration;
        if (moduleConfiguration.getStrategyFactory() == null) {
            ModuleStrategyFactory factory = defineDefaultModuleStrategyFactory();
            if (factory != null) {
                c = new DefaultModuleConfiguration(moduleConfiguration.getConfiguration(), factory);
            }
        }
        return c;
    }

    protected ModuleStrategyFactory defineDefaultModuleStrategyFactory() {
        return null;
    }

    protected void bindConfiguration() {
        bind(ModuleConfiguration.class).toInstance(moduleConfiguration);
        bind(Configuration.class).toInstance(moduleConfiguration.getConfiguration());
    }
}
