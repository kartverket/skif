package no.statkart.skif.skiftest.service.test.tutorial;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import no.statkart.skif.service.proxy.ChainedProxyHandler;
import no.statkart.skif.service.proxy.ProxyHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class OrderedTypeServiceProvider<S> implements Provider<S> {
    private final TypeLiteral<S> type;
    private final ProxyHandler<S> proxyHandler;

    @Inject
    public OrderedTypeServiceProvider(TypeLiteral<S> type, Injector injector,
                                      ToImplementationProxyHandler<S> implementationProxyHandler,
                                      Set<OrderedType<ChainedProxyHandler<S>>> proxyHandlerSet) {
        this.type = type;
        ProxyHandler<S> prev = implementationProxyHandler;
        List<OrderedType<ChainedProxyHandler<S>>> sortedList = new ArrayList<>(proxyHandlerSet);
        Collections.sort(sortedList, (o1, o2) -> (int) Math.signum(o1.getOrder() - o2.getOrder()));

        for (OrderedType<ChainedProxyHandler<S>> next: sortedList ) {
            prev = injector.getInstance(Key.get(next.type)).setChained(prev);
        }
        this.proxyHandler = prev;
    }

    @Override
    public S get() {
        S instanceOrProxy = proxyHandler.buildProxy(type);
        return instanceOrProxy;
    }
}
