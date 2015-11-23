package no.statkart.skif.skiftest.config;


import com.google.inject.Injector;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.module.ModuleBuilder;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;


/**
 * Definere hvilken injector som skal brukes internt i serveren og hvordan denne konfigureres opp.
 */
@Startup
@Singleton
public class SkifTestServerInjectorEJBBean implements SkifTestServerInjector {
    private Injector injector;

    @PostConstruct
    public void init() {
        injector = new ModuleBuilder()
                .setModuleClass(SkifTestServerModule.class)
                .setServiceMode(ServiceMode.JEE)
                .buildInjector();
    }

    @Override
    public Injector getInjector() {
        return injector;
    }
}
