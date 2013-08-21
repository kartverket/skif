package no.statkart.skif.storetest.config;


import com.google.inject.Injector;
import no.statkart.skif.ServerInjectorRegistry;
import no.statkart.skif.config.SkifServerConfiguration;
import no.statkart.skif.module.ModuleBuilder;


/**
 * Definere hvilken injector som skal brukes intern i serveren og hvordan denne konfigureres opp.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class StoreTestServerInjector {
    public static Injector getInjector() {
        ModuleBuilder moduleBuilder = new ModuleBuilder()
                .setConfiguration(new SkifServerConfiguration())
                .setModuleClass(StoreTestServerModule.class);
        return ServerInjectorRegistry.getInjector("SkifTestServerModule", moduleBuilder);
    }
}
