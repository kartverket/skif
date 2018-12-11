package no.statkart.skif.skiftest.service.proxy;


import no.statkart.skif.service.proxy.ChainedProxyHandler;
import no.statkart.skif.service.proxy.ProxyHandler;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ProxyTest {

    @Test
    public void testProxyHandler() {
        A a = new AlwaysXProxyHandler<A>().buildProxy(A.class);
        assertThat(a.hello("Y")).isEqualTo("X");
    }

    @Test
    public void testSetupHandlerChain() {
        ProxyHandler<A> handler = new CachingProxyHandler<A>().setChained(new AlwaysXProxyHandler<>());
        assertThat(getNames(handler)).isEqualTo("CachingProxyHandler->AlwaysXProxyHandler");
        A a = handler.buildProxy(A.class);
        assertThat(a.hello("Y")).isEqualTo("X");
        assertThat(((CachingProxyHandler<?>) handler).getCache()).hasSize(1);
    }

    private String getNames(ProxyHandler<A> h) {
        ProxyHandler next = (h instanceof ChainedProxyHandler) ?
                ((ChainedProxyHandler)h).getChained() : null;
        return h.getClass().getSimpleName() +
                (next==null ? "" : ("->" + getNames(next)));
    }
}

interface A {
    String hello(String message);
}

class AlwaysXProxyHandler<S> extends ProxyHandler<S> {
    @Override
    protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        return "X";
    }
}

class CachingProxyHandler<S> extends ChainedProxyHandler<S> {
    private List cache = new ArrayList();
    @Override
    protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        cache.add(args);
        return chained.invoke(proxy, method, args);
    }
    public List getCache() { return cache;}
}
