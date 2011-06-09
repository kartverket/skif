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
import no.statkart.skif.service.chain.WSServiceChainFactoryDefaultImpl;
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
    protected final Set<Class<? extends Object>> services = new HashSet<Class<? extends Object>>();
    protected final Mapping mapping;
    protected ExceptionMapping exceptionMapping;
    protected Class<? extends ServiceContextMapper<?>> serviceContextMapperClass;
    protected String[] classWSIPackageMappings = {"api:wsapi", "service:wsapi.service"};

    private Class<? extends WSServiceChainFactory> wsServiceChainFactoryClassForWSI = WSServiceChainFactoryDefaultImpl.class;
    private Class<? extends WSServiceChainFactory> wsServiceChainFactoryClassForService = WSServiceChainFactoryDefaultImpl.class;

    public WSServerServiceModule(Configuration configuration, Collection<Class<? extends Object>> services, Mapping mapping) {
        super(WSServerServiceModuleStrategy.class, configuration);
        Preconditions.checkNotNull(mapping, "mapping2");
        this.services.addAll(services);
        this.mapping = mapping;
    }

    public WSServerServiceModule(ModuleConfiguration configuration, Collection<Class<? extends Object>> services, Mapping mapping) {
        super(WSServerServiceModuleStrategy.class, configuration);
        Preconditions.checkNotNull(mapping, "mapping2");
        this.services.addAll(services);
        this.mapping = mapping;
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

    public String[] getClassWSIPackageMappings() {
        return classWSIPackageMappings;
    }

    public void setClassWSIPackageMappings(String[] classWSIPackageMappings) {
        this.classWSIPackageMappings = classWSIPackageMappings;
    }

    @Override
    protected void configure() {
        setStrategyInstance();
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
            Class<? extends ServiceWSI> serviceWSIClass = findWSIClass(serviceClass);
            strategy.bindSkifWSInterceptorForService(outerBinder, innerBinder, serviceClass, serviceWSIClass);
            strategy.bindWSServiceChainFactoryForService(outerBinder, innerBinder, serviceClass, serviceWSIClass);
            strategy.bindService(outerBinder, innerBinder, serviceClass, serviceWSIClass) ;
        }
    }

    protected <S> Class<? extends ServiceWSI> findWSIClass(Class<S> serviceClass) {
        String[] classPackageMappings = {"api:wsapi", "service:wsapi.service"};
        Class<? extends ServiceWSI> webServiceClass = null;
        String serviceClassname = serviceClass.getName();

        List<String> mapingsTried = new ArrayList<String>();
        List<String> classNamesTried = new ArrayList<String>();

        for (String classPackageMapping : classPackageMappings) {
            final String[] mapping = classPackageMapping.split(":");
            if (mapping.length != 2) {
                throw new ConfigurationException("Error in Web Service classmapping. Expected format \"fromPackage:toPackage\":" + classPackageMapping);
            }
            final String fromPackage = mapping[0];
            final String toPackage = mapping[1];
            String webServiceClassname = Pattern.compile(Matcher.quoteReplacement(fromPackage)).matcher(serviceClassname).replaceFirst(toPackage) + "WSI";
            try {
                webServiceClass = (Class<? extends ServiceWSI>) Class.forName(webServiceClassname);
                break; // found class
            } catch (ClassNotFoundException e) {
                mapingsTried.add(classPackageMapping);
                classNamesTried.add(webServiceClassname);
                // Ignore
            }
        }
        if (webServiceClass == null) {
            throw new ConfigurationException("Could not find Web Service class for service: " + serviceClass.getName() + " using packagemappings: " + mapingsTried + ". The following Web Service classes where tried: " + classNamesTried);
        }
        return webServiceClass;
    }

}
