package no.statkart.skif.service.module.common;

import com.google.inject.Binder;
import com.google.inject.PrivateBinder;
import com.google.inject.PrivateModule;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Providers;
import com.google.common.base.Preconditions;
import no.statkart.skif.mapper.ExceptionMapping;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.service.LoginUserHolder;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.service.ServiceContextMapper;
import no.statkart.skif.module.ModuleWithStrategy;
import no.statkart.skif.service.proxy.D2WAdapterProxyHandler;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class RemoteServiceModule extends ModuleWithStrategy<RemoteServiceModuleStrategy> {
    protected final Set<Class<? extends Object>> services = new HashSet<Class<? extends Object>>();
    protected final Mapping mapping;
    protected Class<? extends ServiceContextMapper<?>> serviceContextMapperClass;
    protected ExceptionMapping exceptionMapping;
    /**
     * Package name mapping for strategi.
     * @see RemoteServiceModuleStrategy#classWSPackageMappings
     */
    protected String[] classWSPackageMappings = null;

    public RemoteServiceModule(ModuleConfiguration configuration, Collection<Class<? extends Object>> services, Mapping mapping) {
        super(RemoteServiceModuleStrategy.class, configuration);
        Preconditions.checkNotNull(mapping, "mapping");
        this.services.addAll(services);
        this.mapping = mapping;
    }

    public Mapping getMapping() {
        return mapping;
    }

    public Class<? extends ServiceContextMapper<?>> getServiceContextMapperClass() {
        return serviceContextMapperClass;
    }

    public RemoteServiceModule setServiceContextMapperClass(Class<? extends ServiceContextMapper<?>> serviceContextMapperClass) {
        this.serviceContextMapperClass = serviceContextMapperClass;
        return this;
    }

    public ExceptionMapping getExceptionMapping() {
        return exceptionMapping;
    }

    public RemoteServiceModule setExceptionMapping(ExceptionMapping exceptionMapping) {
        this.exceptionMapping = exceptionMapping;
        return this;
    }

    /**
     * @return {@link #classWSPackageMappings}
     */
    public String[] getClassWSPackageMappings() {
        return classWSPackageMappings;
    }

    /**
     * @see #classWSPackageMappings
     */
    public RemoteServiceModule setClassWSPackageMappings(String... classWSPackageMappings) {
        this.classWSPackageMappings = classWSPackageMappings;
        return this;
    }


    @Override
    protected void configure() {
        requireBindings();
        if (classWSPackageMappings != null) {
            getStrategy().setClassWSPackageMappings(classWSPackageMappings);
        }
        install(new PrivateModule() {
            @Override
            protected void configure() {
                configureMapping(RemoteServiceModule.this.binder(), binder());
                configureServices(RemoteServiceModule.this.binder(), binder());
            }
        });
    }

    protected void requireBindings() {
        requireBinding(LoginUserHolder.class);
        getStrategy().requireBindings(binder());
        if (serviceContextMapperClass != null) {
            requireBinding(ServiceContext.class);
        }
    }

    protected void configureMapping(Binder outerBinder, PrivateBinder innerBinder) {
        innerBinder.bind(Mapping.class).toProvider(Providers.of(mapping));
        innerBinder.bind(ExceptionMapping.class).toProvider(Providers.of(exceptionMapping));
        if (serviceContextMapperClass == null) {
            innerBinder.bind(new TypeLiteral<ServiceContextMapper<?>>() {}).toProvider(Providers.<ServiceContextMapper<?>>of(null));
        } else {
            innerBinder.bind(new TypeLiteral<ServiceContextMapper<?>>() {}).to(serviceContextMapperClass);
        }
    }

    private void configureServices(Binder outerBinder, PrivateBinder innerBinder) {
        final RemoteServiceModuleStrategy strategy = getStrategy();
        for (Class<? extends Object> service : services) {
            strategy.bindCallServiceChainFactoryForService(outerBinder, innerBinder, service);
            strategy.bindService(outerBinder, innerBinder, service);
        }
    }

}
