package no.statkart.skif.module;

import com.google.inject.name.Names;
import no.statkart.skif.service.module.ServerModuleStrategyFactory;
import no.statkart.skif.service.module.server.ServerModule;

/**
 * @author Henrik Fredholm
 * @since 1.1
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
    }

    protected void configureModulename() {
        bind(String.class).annotatedWith(Names.named("modulename")).toInstance("TestServerModule");
    }
}
