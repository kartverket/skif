package no.statkart.skif.config;

import com.google.common.base.Supplier;
import com.google.inject.Injector;
import no.statkart.skif.ServerInjectorRegistry;
import no.statkart.skif.module.ModuleBuilder;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class SkifServerInjector {
    public static Injector getInjector() {
        return ServerInjectorRegistry.getInjector("SkifServerModule", new Supplier<ModuleBuilder>() {
            @Override
            public ModuleBuilder get() {
                return new ModuleBuilder()
                        .setModuleClass(SkifServerModule.class);
            }
        });
    }

}
