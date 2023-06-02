package no.statkart.skif.storetest.config;


import com.google.common.base.Supplier;
import com.google.inject.Injector;
import no.statkart.skif.ServerInjectorRegistry;
import no.statkart.skif.config.SkifServerConfiguration;
import no.statkart.skif.module.ModuleBuilder;

import jakarta.ejb.Stateless;


/**
 * Definere hvilken injector som skal brukes internt i serveren og hvordan denne konfigureres opp.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
@Stateless
public class StoreTestServerInjectorEJBBean implements StoreTestServerInjector {
    @Override
    public Injector getInjector() {
        return ServerInjectorRegistry.getInjector("SkifTestServerModule", new Supplier<ModuleBuilder>() {
            @Override
            public ModuleBuilder get() {
                return new ModuleBuilder()
                        .setConfiguration(new SkifServerConfiguration())
                        .setModuleClass(StoreTestServerModule.class);
            }
        });
    }
}
