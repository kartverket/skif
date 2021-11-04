package no.statkart.skif.skiftest.config;


import com.google.common.base.Supplier;
import com.google.inject.Injector;
import no.statkart.skif.ServerInjectorRegistry;
import no.statkart.skif.config.SkifConfiguration;
import no.statkart.skif.module.ModuleBuilder;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Definere hvilken injector som skal brukes internt i serveren og hvordan denne konfigureres opp.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */

public class SkifTestTxManagementServerInjectorEJBBeanSpring implements SkifTestTxManagementServerInjector {
    @Autowired
    private SkifConfiguration configuration;

    @Override
    public Injector getInjector() {
        return ServerInjectorRegistry.getInjector("StoreTestTxManagementServerModule", new Supplier<ModuleBuilder>() {
            @Override
            public ModuleBuilder get() {
                return new ModuleBuilder()
                        .setConfiguration(configuration)
                        .setModuleClass(SkifTestTxManagementServerModuleSpring.class);
            }
        });
    }
}
