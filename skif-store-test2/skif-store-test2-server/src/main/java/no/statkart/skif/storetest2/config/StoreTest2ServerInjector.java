package no.statkart.skif.storetest2.config;


import com.google.inject.Injector;
import no.statkart.skif.ServerInjectorRegistry;
import no.statkart.skif.config.SkifConfiguration;
import no.statkart.skif.module.ModuleBuilder;


/**
 * Definere hvilken injector som skal brukes intern i serveren og hvordan denne konfigureres opp.
 *
 * @author Henrik Fredholm
 * @since 2.2.0
 */
public class StoreTest2ServerInjector {
    public static Injector getInjector() {
        ModuleBuilder moduleBuilder = new ModuleBuilder()
                .setConfiguration(new SkifConfiguration())
                .setModuleClass(StoreTest2ServerModule.class);
        return ServerInjectorRegistry.getInjector("SkifStoreTest2ServerModule", moduleBuilder);
    }
}
