package no.statkart.skif.module;

import com.google.inject.Singleton;
import com.google.inject.name.Names;
import no.statkart.skif.SkifModule;
import no.statkart.skif.config.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Guice Module for å teste konfigurasjonsrammeverket. Inneholder en singleton liste
 * ved navn "test"  man kan hente ut og teste på.
 *
 * @author Henrik Fredholm
 * @since 0.4
 */
public class TestModule extends SkifModule {

    public TestModule(ModuleConfiguration moduleConfiguration) {
        super(moduleConfiguration);
    }



    @Override
    protected void configure() {
        bind(List.class).annotatedWith(Names.named("test")).to(ArrayList.class).in(Singleton.class);
        bind(Configuration.class).toInstance(moduleConfiguration.getConfiguration());
        configureModulename();
    }

    protected void configureModulename() {
        bind(String.class).annotatedWith(Names.named("modulename")).toInstance("TestModule");
    }

}
