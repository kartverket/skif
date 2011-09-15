package no.statkart.skif.config;

import com.google.inject.Injector;
import no.statkart.skif.ServerInjectorRegistry;
import no.statkart.skif.module.ModuleBuilder;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class SkifServerInjector {

    public static Injector getInjector() {
        ModuleBuilder moduleBuilder = new ModuleBuilder()
                .setModuleClass(SkifServerModule.class);
        return ServerInjectorRegistry.getInjector("SkifServerModule", moduleBuilder);
    }

}
