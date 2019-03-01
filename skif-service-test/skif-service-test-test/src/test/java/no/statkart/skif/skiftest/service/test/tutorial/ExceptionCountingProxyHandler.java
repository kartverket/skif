package no.statkart.skif.skiftest.service.test.tutorial;

import no.statkart.skif.service.proxy.ChainedProxyHandler;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

public class ExceptionCountingProxyHandler<S> extends ChainedProxyHandler<S> {
    final AtomicInteger counter = new AtomicInteger();
    @Override
    protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            return chained.invoke(proxy, method, args);
        } catch (Throwable e) {
            counter.incrementAndGet();
            throw e;
        } finally {
            System.out.println("ExceptionCountingHandler<" + method.getDeclaringClass().getSimpleName() + ">: " + counter.get());
        }
    }

}
