package no.statkart.skif.service.module.server;

import com.google.inject.Binder;
import com.google.inject.PrivateBinder;
import com.google.inject.PrivateModule;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Providers;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.exception.ConfigurationException;
import no.statkart.skif.guava.Preconditions;
import no.statkart.skif.mapper.ExceptionMapping;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleWithStrategy;
import no.statkart.skif.service.ServiceContextMapper;
import no.statkart.skif.service.chain.WSServiceChainFactory;
import no.statkart.skif.service.chain.WSServiceChainFactoryBase;
import no.statkart.skif.service.ws.ServiceWSI;

import javax.annotation.Nullable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public class WSServerServiceModule extends ModuleWithStrategy<WSServerServiceModuleStrategy> {
    protected final ClassLoader classLoader;
    protected final Set<Class<? extends Object>> services = new HashSet<Class<? extends Object>>();
    protected final Mapping mapping;
    protected ExceptionMapping exceptionMapping;
    protected Class<? extends ServiceContextMapper<?>> serviceContextMapperClass;
    /**
     * Package name mapping for strategi.
     * @see WSServerServiceModuleStrategy#classWSIPackageMappings
     */
    protected String[] classWSIPackageMappings = null;

    private Class<? extends WSServiceChainFactory> wsServiceChainFactoryClassForWSI = WSServiceChainFactoryBase.class;
    private Class<? extends WSServiceChainFactory> wsServiceChainFactoryClassForService = WSServiceChainFactoryBase.class;

    public WSServerServiceModule(Configuration configuration, Collection<Class<? extends Object>> services, Mapping mapping) {
        super(WSServerServiceModuleStrategy.class, configuration);
        Preconditions.checkNotNull(mapping, "mapping");
        this.services.addAll(services);
        this.mapping = mapping;
        this.classLoader = getClass().getClassLoader();
    }

    public WSServerServiceModule(Configuration configuration, Collection<Class<? extends Object>> services, Mapping mapping, ClassLoader classLoader) {
        super(WSServerServiceModuleStrategy.class, configuration);
        Preconditions.checkNotNull(mapping, "mapping");
        this.services.addAll(services);
        this.mapping = mapping;
        this.classLoader =classLoader;
    }
    public WSServerServiceModule(ModuleConfiguration configuration, Collection<Class<? extends Object>> services, Mapping mapping) {
        super(WSServerServiceModuleStrategy.class, configuration);
        Preconditions.checkNotNull(mapping, "mapping");
        this.services.addAll(services);
        this.mapping = mapping;
        this.classLoader =getClass().getClassLoader();
    }

    public WSServerServiceModule(ModuleConfiguration configuration, Collection<Class<? extends Object>> services, Mapping mapping, ClassLoader classLoader) {
        super(WSServerServiceModuleStrategy.class, configuration);
        Preconditions.checkNotNull(mapping, "mapping");
        this.services.addAll(services);
        this.mapping = mapping;
        this.classLoader = classLoader;
    }

    public ExceptionMapping getExceptionMapping() {
        return exceptionMapping;
    }

    public WSServerServiceModule setExceptionMapping(@Nullable ExceptionMapping exceptionMapping) {
        this.exceptionMapping = exceptionMapping;
        return this;
    }

    public Class<? extends ServiceContextMapper<?>> getServiceContextMapperClass() {
        return serviceContextMapperClass;
    }

    public WSServerServiceModule setServiceContextMapperClass(@Nullable Class<? extends ServiceContextMapper<?>> serviceContextMapperClass) {
        this.serviceContextMapperClass = serviceContextMapperClass;
        return this;
    }

    /**
     * @return {@link #classWSIPackageMappings}
     */

    public String[] getClassWSIPackageMappings() {
        return classWSIPackageMappings;
    }

    /**
     * @see #classWSIPackageMappings
     */
    public WSServerServiceModule setClassWSIPackageMappings(String... classWSIPackageMappings) {
        this.classWSIPackageMappings = classWSIPackageMappings;
        return this;
    }

    @Override
    protected void configure() {
        setStrategyInstance();
        if (classWSIPackageMappings != null) {
            getStrategy().setClassWSIPackageMappings(classWSIPackageMappings);
        }
        Preconditions.checkArgument(moduleConfiguration.getServiceMode() == ServiceMode.JEE, "Kun ServiceMode.JEE er støttet");
        requireBindings();
        install(new PrivateModule() {
            @Override
            protected void configure() {
                configureMapping(WSServerServiceModule.this.binder(), binder());
                configureExceptionMapping(WSServerServiceModule.this.binder(), binder());
                configureServiceContextMapper(WSServerServiceModule.this.binder(), binder());
                configureServices(WSServerServiceModule.this.binder(), binder());
            }
        });
    }

    protected void requireBindings() {
    }

    protected void configureMapping(Binder outerBinder, PrivateBinder innerBinder) {
        innerBinder.bind(Mapping.class).toProvider(Providers.of(mapping));
    }

    protected void configureExceptionMapping(Binder outerBinder, PrivateBinder innerBinder) {
        innerBinder.bind(ExceptionMapping.class).toProvider(Providers.of(exceptionMapping));
    }

    protected void configureServiceContextMapper(Binder outerBinder, PrivateBinder innerBinder) {
        if (serviceContextMapperClass == null) {
            innerBinder.bind(new TypeLiteral<ServiceContextMapper<?>>() {
            }).toProvider(Providers.<ServiceContextMapper<?>>of(null));
        } else {
            innerBinder.bind(new TypeLiteral<ServiceContextMapper<?>>() {
            }).to(serviceContextMapperClass);
        }
    }

    protected void configureServices(Binder outerBinder, PrivateBinder innerBinder) {
        for (Class<? extends Object> serviceClass : services) {
            Class<? extends ServiceWSI> serviceWSIClass = strategy.findWSIClass(serviceClass, classLoader);
            strategy.bindSkifWSInterceptorForService(outerBinder, innerBinder, serviceClass, serviceWSIClass);
            strategy.bindWSServiceChainFactoryForService(outerBinder, innerBinder, serviceClass, serviceWSIClass);
            strategy.bindService(outerBinder, innerBinder, serviceClass, serviceWSIClass) ;
        }
    }

}
