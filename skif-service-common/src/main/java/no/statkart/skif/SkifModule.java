package no.statkart.skif;

import com.google.inject.AbstractModule;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.module.DefaultModuleConfiguration;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleStrategyFactory;

/**
 * @author Henrik Fredholm
 * @since 2.0
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

    /**
     * Adding default ModuleStrategyFactory when not provided by configuration.
     * 
     * @see no.statkart.skif.module.ModuleBuilder#getModuleStrategyFactoryClassname
     */
    protected final ModuleConfiguration addDefaultFactory(ModuleConfiguration moduleConfiguration) {
        if (moduleConfiguration.getStrategyFactory() == null) {
            ModuleStrategyFactory factory = defineDefaultModuleStrategyFactory(moduleConfiguration);
            if (factory != null) {
                return new DefaultModuleConfiguration(moduleConfiguration.getConfiguration(), factory);
            }
        }
        return moduleConfiguration;
    }
    
    protected ModuleStrategyFactory defineDefaultModuleStrategyFactory(ModuleConfiguration moduleConfiguration) {
        return defineDefaultModuleStrategyFactory();
    }

    /**
     * @deprecated Bruk defineDefaultModuleStrategyFactory som tar inn moduleConfiguration som parameter i stedet!
     */
    @Deprecated(since = "2.10", forRemoval = true)
    protected ModuleStrategyFactory defineDefaultModuleStrategyFactory() {
        return null;
    }
}
