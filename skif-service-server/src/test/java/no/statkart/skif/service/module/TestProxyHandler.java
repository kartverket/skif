package no.statkart.skif.service.module;

import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import no.statkart.skif.service.proxy.ChainedProxyHandler;

import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * User: strant
 * Date: 23.11.12
 * Time: 15:07
 * To change this template use File | Settings | File Templates.
 */
class TestProxyHandler<S> extends ChainedProxyHandler<S> {
    @Inject
    private TypeLiteral<S> type;

    public TypeLiteral<S> getType() {
        return type;
    }

    @Override
    protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        return chained.invoke(proxy, method, args);
    }
}
