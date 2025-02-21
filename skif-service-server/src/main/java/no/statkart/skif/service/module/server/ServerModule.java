package no.statkart.skif.service.module.server;

import com.google.inject.Provides;
import jakarta.transaction.TransactionManager;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleWithStrategy;
import no.statkart.skif.service.CallIdProvider;
import no.statkart.skif.service.DefaultServiceContext;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.service.annotation.CallId;
import no.statkart.skif.service.scope.ServiceRequestScope;
import no.statkart.skif.service.scope.ServiceRequestScoped;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionContext;

/**
 * @since 2.0
 */
public class ServerModule extends ModuleWithStrategy<ServerModuleStrategy> {
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

        // Denne seedes inn i ServiceRequestScope
        bind(TransactionManager.class).toProvider(ServiceRequestScope.<TransactionManager>seededKeyProvider()).in(ServiceRequestScoped.class);

        // Binder opp provider av call id
        bind(Long.class).annotatedWith(CallId.class).toProvider(CallIdProvider.class);

        // Standard bindinger som må være med
        bind(ServiceMode.class).toInstance(moduleConfiguration.getServiceMode());
        bind(ServiceContext.class).to(serviceContextClass);
        bind(serviceContextClass).in(ServiceRequestScoped.class);
        bind(Configuration.class).toInstance(moduleConfiguration.getConfiguration());
        bind(ModuleConfiguration.class).toInstance(moduleConfiguration);

        // SnapshotVersionContext har trådlokal verdi og bindes opp som ekte singleton istedet for med ServiceRequest scope
        bind(SnapshotVersionContext.class).toInstance(SnapshotVersionContext.getInstance());

        // ServiceMode avhengige bindinger
        strategy.configure(binder());
    }

    @Provides
    SnapshotVersion snapshotVersionProvider(SnapshotVersionContext snapshotVersionContext) {
        return snapshotVersionContext.getSnapshotVersion();
    }

}
