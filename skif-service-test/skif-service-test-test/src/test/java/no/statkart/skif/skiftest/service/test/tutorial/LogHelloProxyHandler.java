package no.statkart.skif.skiftest.service.test.tutorial;

import no.statkart.skif.service.proxy.ChainedProxyHandler;

import java.lang.reflect.Method;

public class LogHelloProxyHandler<S> extends ChainedProxyHandler<S> {

    @Override
    protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            return chained.invoke(proxy, method, args);
        } finally {
            System.out.println("Hello");
        }
    }

}
