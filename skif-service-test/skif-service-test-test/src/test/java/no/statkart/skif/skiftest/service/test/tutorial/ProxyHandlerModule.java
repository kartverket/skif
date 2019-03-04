package no.statkart.skif.skiftest.service.test.tutorial;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.service.chain.CallServiceChainFactory;
import no.statkart.skif.service.proxy.ProxyHandler;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ProxyHandlerModule extends AbstractModule {
    public static Class ServicePlaceholder = new TypeLiteral<Object>(){}.getRawType();
    private final Class<? extends ProxyHandler> proxyHandler;
    private float order;
    private final Set<Class<?>> services = new HashSet<>();
    public ProxyHandlerModule(Class<? extends ProxyHandler> proxyHandler, float order, Class<?>... services) {
        this(proxyHandler, order, Arrays.asList(services));
    }

    public ProxyHandlerModule(Class<? extends ProxyHandler> proxyHandler, float order, Collection<Class<?>> services) {
        this.proxyHandler = proxyHandler;
        this.order = order;
        this.services.addAll(services);
    }

    @Override
    protected void configure() {
        for (Class<? extends Object> service : services) {
            bindProxyHandlerToService(service);
        }
    }

    private <S> void bindProxyHandlerToService(Class<S> service) {
        //Multibinder<CallServiceChainFactory<MyService>> multibinder
        //        = Multibinder.newSetBinder(binder(), new TypeLiteral<CallServiceChainFactory<MyService>>() {});
        TypeLiteral<CallServiceChainFactory<S>> callServiceChainFactoryType = SkifUtil.typeLiteral(CallServiceChainFactory.class, service);
        Multibinder<CallServiceChainFactory<S>> multibinder
                = Multibinder.newSetBinder(binder(), callServiceChainFactoryType);
        //multibinder.addBinding().toProvider(new CallServiceChainFactoryProvider<>(new TypeLiteral<MyProxyHandlerImplementation<MyService>>() {}, 0.5f));
        TypeLiteral<ProxyHandler<S>> proxyHandlerType = SkifUtil.typeLiteral(proxyHandler, service);
        multibinder.addBinding().toProvider(new CallServiceChainFactoryProvider<>(proxyHandlerType, order));
    }

}
