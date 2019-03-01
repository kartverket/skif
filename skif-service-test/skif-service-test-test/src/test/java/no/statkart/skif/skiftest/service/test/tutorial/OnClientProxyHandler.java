package no.statkart.skif.skiftest.service.test.tutorial;

import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import no.statkart.skif.service.proxy.ChainedProxyHandler;

import java.lang.reflect.Method;

public class OnClientProxyHandler<S> extends ChainedProxyHandler<S> {
    final private TypeLiteral<S> type;

    @Inject
    public OnClientProxyHandler(TypeLiteral<S> type) {
        this.type = type;
    }

    @Override
    protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            System.out.println("OnClientProxyHandler<" + type.getRawType().getSimpleName() + ">." + method.getName() + "- Begin");
            return chained.invoke(proxy, method, args);
        } finally {
            System.out.println("OnClientProxyHandler<" + type.getRawType().getSimpleName() + ">." + method.getName() + "- End");
        }
    }

}
