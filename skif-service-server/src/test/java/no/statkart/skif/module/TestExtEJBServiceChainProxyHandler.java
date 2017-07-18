package no.statkart.skif.module;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import no.statkart.skif.service.proxy.ChainedProxyHandler;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class TestExtEJBServiceChainProxyHandler<S> extends ChainedProxyHandler<S> {

    private final Provider<List> listProvider;

    @Inject
    public TestExtEJBServiceChainProxyHandler(@Named("testExt2") Provider<List> listProvider) {
        this.listProvider = listProvider;
    }

    @Override
    protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        listProvider.get().add("Inserted by TestExtEJBServiceChainProxyHandler");
        try {
            //noinspection UnnecessaryLocalVariable
            Object result = chained.invoke(proxy, method, args);
            return result;
        } finally {
            listProvider.get().clear();
        }
    }
}
