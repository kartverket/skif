package no.statkart.skif.skiftest.config;


import com.google.inject.Injector;
import no.statkart.skif.ServerInjectorRegistry;
import no.statkart.skif.config.SkifServerConfiguration;
import no.statkart.skif.module.ModuleBuilder;

import javax.ejb.Stateless;


/**
 * Definere hvilken injector som skal brukes intern i serveren og hvordan denne konfigureres opp.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
@Stateless
public class SkifTestTxManagementServerInjectorEJBBean implements SkifTestTxManagementServerInjector {
    @Override
    public Injector getInjector() {
        return ServerInjectorRegistry.getInjector("StoreTestTxManagementServerModule", () -> new ModuleBuilder()
                .setConfiguration(new SkifServerConfiguration())
                .setModuleClass(SkifTestTxManagementServerModule.class)
        );
    }
}
