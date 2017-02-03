package no.statkart.skif.module;

import com.google.inject.Singleton;
import com.google.inject.name.Names;
import no.statkart.skif.persistence.jdbc.DummyDataSourceModule;
import no.statkart.skif.service.module.ServerModuleStrategyFactory;
import no.statkart.skif.service.module.server.ServerModule;

import java.util.ArrayList;
import java.util.List;

/**
 * En meget enkel ServerModule som bruke for unit testing.
 * @author Henrik Fredholm
 * @since 2.0
 */
public class TestServerModule extends TestModule {
    public TestServerModule(ModuleConfiguration moduleConfiguration) {
        super(moduleConfiguration);
    }

    @Override
    protected ModuleStrategyFactory defineDefaultModuleStrategyFactory() {
        return new ServerModuleStrategyFactory();
    }

    @Override
    protected void configure() {
        super.configure();
        install(new ServerModule(moduleConfiguration));
        install(new DummyDataSourceModule());
    }

    protected void configureModulename() {
        bind(List.class).annotatedWith(Names.named("test")).to(ArrayList.class).in(Singleton.class); // OBS! Her er det kun bindingen fra @Named List som er singleton, ikke klassen ArrayList
        bind(String.class).annotatedWith(Names.named("modulename")).toInstance("TestServerModule");
    }
}
