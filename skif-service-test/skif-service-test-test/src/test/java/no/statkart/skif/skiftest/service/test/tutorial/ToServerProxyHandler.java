package no.statkart.skif.skiftest.service.test.tutorial;

import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import no.statkart.skif.service.SingleVmServer;
import no.statkart.skif.service.proxy.TerminatingProxyHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ToServerProxyHandler<S> extends TerminatingProxyHandler<S> {
    final private TypeLiteral<S> serviceType;
    final private SingleVmServer singleVmServer;

    @Inject
    public ToServerProxyHandler(TypeLiteral<S> serviceType, SingleVmServer singleVmServer) {
        this.serviceType = serviceType;
        this.singleVmServer = singleVmServer;
    }

    @Override
    protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            S serviceInstanceOnServer = singleVmServer.getInjector().getInstance(Key.get(serviceType));
            return method.invoke(serviceInstanceOnServer, args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

}
