package no.statkart.skif.wsversioning.config;

import com.google.inject.Injector;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import no.statkart.skif.module.ModuleConfiguration;

/**
 * Definere hvilken injector som skal brukes internt i V1-api-et og hvordan denne konfigureres opp.
 */
@Startup
@Singleton
public class WSVersioningServerV1InjectorEJBBean implements WSVersioningServerV1Injector {
    @EJB
    private WSVersioningServerInjector wsVersioningServerInjector;

    private Injector injector;

    @PostConstruct
    public void init() {
        Injector parentInjector = wsVersioningServerInjector.getInjector();
        ModuleConfiguration moduleConfiguration = parentInjector.getInstance(ModuleConfiguration.class);

        injector = parentInjector.createChildInjector(
                new WSVersioningV1ServerModule(moduleConfiguration)
        );
    }

    @Override
    public Injector getInjector() {
        return injector;
    }
}
