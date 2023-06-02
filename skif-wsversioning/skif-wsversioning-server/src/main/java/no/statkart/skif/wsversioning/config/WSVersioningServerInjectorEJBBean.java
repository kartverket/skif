package no.statkart.skif.wsversioning.config;

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
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
@Stateless
public class WSVersioningServerInjectorEJBBean implements WSVersioningServerInjector {
    @Override public Injector getInjector() {
        return ServerInjectorRegistry.getInjector("WSVersioningServerModule", new Supplier<ModuleBuilder>() {
            @Override
            public ModuleBuilder get() {
                return new ModuleBuilder()
                        .setConfiguration(new SkifServerConfiguration())
                        .setModuleClass(WSVersioningServerModule.class);
            }
        });
    }
}
