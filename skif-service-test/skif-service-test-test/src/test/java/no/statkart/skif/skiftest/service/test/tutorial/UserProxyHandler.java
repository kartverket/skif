package no.statkart.skif.skiftest.service.test.tutorial;

import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import no.statkart.skif.service.proxy.ChainedProxyHandler;

import java.lang.reflect.Method;

public class UserProxyHandler<S> extends ChainedProxyHandler<S> {
    final private TypeLiteral<S> type;
    final private User user;

    @Inject
    public UserProxyHandler(TypeLiteral<S> type, User user) {
        this.type = type;
        this.user = user;
    }

    @Override
    protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("UserProxyHandler<" + type.getRawType().getSimpleName() + ">: " + user.getUsername());
        return chained.invoke(proxy, method, args);
    }

}
