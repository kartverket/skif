package no.statkart.skif.service.module.server;

import no.statkart.skif.ServiceMode;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleWithStrategy;
import no.statkart.skif.service.DefaultServiceContext;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.service.scope.ServiceRequestScope;
import no.statkart.skif.service.scope.ServiceRequestScoped;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class ServerModule extends ModuleWithStrategy<ServerModuleStrategy > {
    private Class<? extends ServiceContext> serviceContextClass = DefaultServiceContext.class;

    public ServerModule(ModuleConfiguration configuration) {
        super(ServerModuleStrategy.class, configuration);
    }

    public Class<? extends ServiceContext> getServiceContextClass() {
        return serviceContextClass;
    }

    public ServerModule setServiceContextClass(Class<? extends ServiceContext> serviceContextClass) {
        this.serviceContextClass = serviceContextClass;
        return this;
    }

    @Override
    protected void configure() {
        setStrategyInstance();

        // Definer ServiceRequestScope og bind til instans (dvs singleton)
        ServiceRequestScope serviceRequestScope = new ServiceRequestScope();
        bindScope(ServiceRequestScoped.class, serviceRequestScope);
        bind(ServiceRequestScope.class).toInstance(serviceRequestScope);
        bind(ServiceRequestContext.class).in(ServiceRequestScoped.class);

        // Standard bindinger som må være med
        bind(ServiceMode.class).toInstance(moduleConfiguration.getServiceMode());
        bind(ServiceContext.class).to(serviceContextClass);
        bind(serviceContextClass).in(ServiceRequestScoped.class);
        bind(Configuration.class).toInstance(moduleConfiguration.getConfiguration());
        bind(ModuleConfiguration.class).toInstance(moduleConfiguration);

        // ServiceMode avhengige bindinger
        strategy.configure(binder());
    }

}
