package no.statkart.skif.skiftest.service.test.tutorial;

import com.google.inject.Inject;
import no.statkart.skif.service.annotation.Implementation;
import no.statkart.skif.service.proxy.TerminatingProxyHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ToImplementationProxyHandler<S> extends TerminatingProxyHandler<S> {
    final S implementation;

    @Inject
    public ToImplementationProxyHandler(@Implementation S implementation) {
        this.implementation = implementation;
    }

    @Override
    protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            return method.invoke(implementation, args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

}
