package no.statkart.skif.service.module.client;

import com.google.inject.Singleton;
import no.statkart.skif.SkifModule;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.service.DefaultServiceContext;
import no.statkart.skif.service.ServiceContext;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class ClientModule extends SkifModule {
    private Class<? extends ServiceContext> serviceContextClass = DefaultServiceContext.class;

    public ClientModule(ModuleConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected void configure() {
        bind(ServiceContext.class).to(DefaultServiceContext.class).in(Singleton.class);
    }
}
