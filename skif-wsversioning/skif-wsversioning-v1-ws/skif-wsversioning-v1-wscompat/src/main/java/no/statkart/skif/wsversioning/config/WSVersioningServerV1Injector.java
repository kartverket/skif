package no.statkart.skif.wsversioning.config;

import com.google.inject.Injector;
import no.statkart.skif.ServerInjectorRegistry;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.service.module.server.ServerServiceModule;

/**
 * Definere hvilken injector som skal brukes internt i V1-api-et og hvordan denne konfigureres opp.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class WSVersioningServerV1Injector {
    public static Injector getInjector() {
        Injector parentInjector = WSVersioningServerInjector.getInjector();
        ModuleConfiguration moduleConfiguration = parentInjector.getInstance(ModuleConfiguration.class);
        return ServerInjectorRegistry.getInjector("WSVersioningServerModuleV1", parentInjector,
                new WSVersioningV1ServerModule(moduleConfiguration)
        );
    }
}
