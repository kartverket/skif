package no.statkart.skif.skiftest.service.test.tutorial;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.service.annotation.Implementation;
import no.statkart.skif.service.provider.ServiceProvider;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ServiceModule extends AbstractModule {
    private final Set<Class<?>> services = new HashSet<>();

    public ServiceModule(Class<?>... services) {
        this(Arrays.asList(services));
    }

    public ServiceModule(Collection<Class<?>>services) {
        this.services.addAll(services);
    }
    @SuppressWarnings("unchecked")
    private <T> Class<? extends T> getImplementation(Class<T> serviceInterface) {
        try {
            return (Class<? extends T>) Class.forName(serviceInterface.getName() + "Impl");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void configure() {
        for (Class<? extends Object> service : services) {
            bindService(service);
        }
    }

    private <S> void bindService(Class<S> service) {
        //bind(MyService.class).annotatedWith(Implementation.class).to(MyServiceImpl.class);
        bind(service).annotatedWith(Implementation.class).to(getImplementation(service));
        //bind(MyService.class).toProvider(new TypeLiteral<ServiceProvider<MyService>>() {});
        TypeLiteral<ServiceProvider<S>> callChainProxyHandlerType = SkifUtil.typeLiteral(ServiceProvider.class, service);
        bind(service).toProvider(callChainProxyHandlerType);
    }
}
