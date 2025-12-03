package no.statkart.skif.service.module.client;

import com.google.inject.Singleton;
import no.statkart.skif.SkifModule;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.service.DefaultServiceContext;
import no.statkart.skif.service.ServiceContext;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class ClientModule extends SkifModule {

    public ClientModule(ModuleConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected ModuleStrategyFactory defineDefaultModuleStrategyFactory(ModuleConfiguration moduleConfiguration) {
        return new ClientModuleStrategyFactory();
    }
    @Override
    protected void configure() {
        bind(ServiceContext.class).to(DefaultServiceContext.class);
        bind(DefaultServiceContext.class).in(Singleton.class);
    }
}
