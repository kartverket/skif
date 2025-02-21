package no.statkart.skif.service.module.server;

import no.statkart.skif.ServiceMode;
import no.statkart.skif.SkifModule;
import no.statkart.skif.config.Configuration;
import com.google.common.base.Preconditions;
import no.statkart.skif.module.ModuleConfiguration;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class WSServerModule extends SkifModule {
    private ClassLoader classLoader = getClass().getClassLoader();

    public WSServerModule(ModuleConfiguration moduleConfiguration, ClassLoader classLoader) {
        super(moduleConfiguration);
        this.classLoader = classLoader;
    }

    public WSServerModule(ModuleConfiguration moduleConfiguration) {
        super(moduleConfiguration);
        this.classLoader = getClass().getClassLoader();
    }

    public WSServerModule(Configuration configuration, ClassLoader classLoader) {
        super(configuration);
        this.classLoader = classLoader;
    }

    public WSServerModule(Configuration configuration) {
        super(configuration);
        this.classLoader = getClass().getClassLoader();
    }
    @Override
    protected void configure() {
        Preconditions.checkArgument(moduleConfiguration.getServiceMode()== ServiceMode.JEE, "Kun ServiceMode.JEE er støttet");
    }
}
