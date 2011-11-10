package no.statkart.skif.skiftest.config;


import com.google.inject.Injector;
import no.statkart.skif.ServerInjectorRegistry;
import no.statkart.skif.module.ModuleBuilder;


/**
 * Definere hvilken injector som skal brukes intern i serveren og hvordan denne konfigureres opp.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class SkifTestServerInjector {
    public static Injector getInjector() {
        ModuleBuilder moduleBuilder = new ModuleBuilder()
                .setModuleClass(SkifTestServerModule.class);
        return ServerInjectorRegistry.getInjector("SkifTestServerModule", moduleBuilder);
    }
}
