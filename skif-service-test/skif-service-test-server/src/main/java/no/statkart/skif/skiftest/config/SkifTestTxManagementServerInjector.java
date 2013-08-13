package no.statkart.skif.skiftest.config;


import com.google.inject.Injector;
import no.statkart.skif.ServerInjectorRegistry;
import no.statkart.skif.config.SkifConfiguration;
import no.statkart.skif.module.ModuleBuilder;


/**
 * Definere hvilken injector som skal brukes intern i serveren og hvordan denne konfigureres opp.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class SkifTestTxManagementServerInjector {
    public static Injector getInjector() {
        ModuleBuilder moduleBuilder = new ModuleBuilder()
                // TODO: bør vi bruke skif-server-default.properteis
                .setConfiguration(new SkifConfiguration("skif-default.properties", "skif-server.properties"))
                .setModuleClass(SkifTestTxManagementServerModule.class);
        return ServerInjectorRegistry.getInjector("StoreTestTxManagementServerModule", moduleBuilder);
    }
}
