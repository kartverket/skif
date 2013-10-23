package no.statkart.skif.wsversioning.config;

import com.google.common.base.Supplier;
import com.google.inject.Injector;
import no.statkart.skif.ServerInjectorRegistry;
import no.statkart.skif.module.ModuleConfiguration;

/**
 * Definere hvilken injector som skal brukes internt i V1-api-et og hvordan denne konfigureres opp.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class WSVersioningServerV1Injector {
    private static final Supplier<Injector> injectorBuilder = new Supplier<Injector>() {
        @Override
        public Injector get() {
            Injector parentInjector = WSVersioningServerInjector.getInjector();
            ModuleConfiguration moduleConfiguration = parentInjector.getInstance(ModuleConfiguration.class);

            return parentInjector.createChildInjector(
                    new WSVersioningV1ServerModule(moduleConfiguration)
            );
        }
    };

    public static Injector getInjector() {
        return ServerInjectorRegistry.getInjectorCustom("WSVersioningServerModuleV1", injectorBuilder);
    }
}
