package no.statkart.skif.module;

import com.google.inject.name.Names;
import no.statkart.skif.service.module.ClientModuleStrategyFactory;
import no.statkart.skif.service.module.common.RemoteServerModule;


/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public class TestClientModule extends TestModule {
    public TestClientModule(ModuleConfiguration moduleConfiguration) {
        super(moduleConfiguration);
    }

    @Override
    protected ModuleStrategyFactory defineDefaultModuleStrategyFactory() {
        return new ClientModuleStrategyFactory();
    }

    @Override
    protected void configure() {
        super.configure();
        install(new RemoteServerModule(moduleConfiguration));
    }

    @Override
    protected void configureModulename() {
        bind(String.class).annotatedWith(Names.named("modulename")).toInstance("TestClientModule");
    }
}
