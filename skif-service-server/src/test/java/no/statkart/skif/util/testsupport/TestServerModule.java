package no.statkart.skif.util.testsupport;

import com.google.inject.name.Names;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.service.module.ServerModuleStrategyFactory;
import no.statkart.skif.service.module.server.ServerModule;

/**
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
    }

    private void configurModulename() {
        bind(String.class).annotatedWith(Names.named("modulename")).toInstance("TestServerModule");
    }
}
