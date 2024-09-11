package no.statkart.skif.wsversioning.config;

import com.google.inject.Injector;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.config.SkifServerConfiguration;
import no.statkart.skif.module.ModuleBuilder;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

/**
 * Definere hvilken injector som skal brukes internt i serveren og hvordan denne konfigureres opp.
 */
@Startup
@Singleton
public class WSVersioningServerInjectorEJBBean implements WSVersioningServerInjector {
    private Injector injector;

    @PostConstruct
    public void init() {
        injector = new ModuleBuilder()
                .setConfiguration(new SkifServerConfiguration())
                .setModuleClass(WSVersioningServerModule.class)
                .setServiceMode(ServiceMode.JEE)
                .buildInjector();
    }

    @Override
    public Injector getInjector() {
        return injector;
    }
}
