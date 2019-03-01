package no.statkart.skif.skiftest.service.test.tutorial;

import no.statkart.skif.service.proxy.ChainedProxyHandler;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

public class CallCountingProxyHandler<S> extends ChainedProxyHandler<S> {
    final AtomicInteger counter = new AtomicInteger();

    @Override
    protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            return chained.invoke(proxy, method, args);
        } finally {
            int i = counter.incrementAndGet();
            System.out.println("CallCountingHandler<" + method.getDeclaringClass().getSimpleName() + ">: " + i);
        }
    }

}
