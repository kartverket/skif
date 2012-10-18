package no.statkart.skif.service.module.client;

import no.statkart.skif.config.SkifConfigConstants;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.service.module.common.RemoteServerModule;
import no.statkart.skif.service.module.common.RunOnServerRemoteServiceModule;

/**
 * Klientmodul som brukes av {@code RunOnRemoteServerBuilder} til å sette opp en tom klient modul som kun
 * understøtter tjenestene {@code ContainerManagedTransactionRunOnServerService} og
 * {@code BeanManagedTransactionRunOnServerService}.
 * @author Henrik Fredholm
 * @since 2.1
 */
public class RunOnRemoteServerClientModule extends ClientModule {
    final Class<? extends ServiceContext> serviceContextClass;

    public RunOnRemoteServerClientModule(ModuleConfiguration moduleConfiguration) {
        this(moduleConfiguration, null);
    }

    protected RunOnRemoteServerClientModule(ModuleConfiguration moduleConfiguration, Class<? extends ServiceContext> serviceContextClass ) {
        super(moduleConfiguration);
        this.serviceContextClass = serviceContextClass;
    }

    @Override
    protected void configure() {
        install(new RemoteServerModule(moduleConfiguration).setServiceContextClass(serviceContextClass));
        install(new RunOnServerRemoteServiceModule(moduleConfiguration));
    }
}
