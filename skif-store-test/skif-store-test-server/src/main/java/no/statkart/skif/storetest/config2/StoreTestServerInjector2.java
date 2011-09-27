package no.statkart.skif.storetest.config2;


import com.google.inject.Injector;
import no.statkart.skif.ServerInjectorRegistry;
import no.statkart.skif.module.ModuleBuilder;


/**
 * Definere hvilken injector som skal brukes intern i serveren og hvordan denne konfigureres opp.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class StoreTestServerInjector2 {
    public static Injector getInjector() {
        ModuleBuilder moduleBuilder = new ModuleBuilder()
                .setModuleClass(StoreTestServerModule2.class);
        return ServerInjectorRegistry.getInjector("SkifTestServerModule2", moduleBuilder);
    }
}
