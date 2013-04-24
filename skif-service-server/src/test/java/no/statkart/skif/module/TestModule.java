package no.statkart.skif.module;

import com.google.inject.Singleton;
import com.google.inject.name.Names;
import no.statkart.skif.SkifConfigurationModule;
import no.statkart.skif.SkifModule;
import no.statkart.skif.config.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Guice Module for å teste konfigurasjonsrammeverket. Inneholder en singleton liste
 * ved navn "test"  man kan hente ut og teste på. Har også en hjelpemetode
 * {@link #configureModulename()} som binder opp binder opp en konstant 'modulename' som kan
 * brukes for testing. Subklasser av denne modulen overskriver denne metoden slik at verdien blir
 * forskjellig for hver subklasse.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class TestModule extends SkifModule {

    public TestModule(ModuleConfiguration moduleConfiguration) {
        super(moduleConfiguration);
    }



    @Override
    protected void configure() {
        install(new SkifConfigurationModule(moduleConfiguration));
        bind(List.class).annotatedWith(Names.named("test")).to(ArrayList.class).in(Singleton.class); // OBS! Her er det kun bindingen fra @Named List som er singleton, ikke klassen ArrayList
        configureModulename();
    }

    protected void configureModulename() {
        bind(String.class).annotatedWith(Names.named("modulename")).toInstance("TestModule");
    }

}
