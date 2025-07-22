package no.statkart.skif.service.ejb;

import com.google.inject.Inject;
import com.google.inject.Provider;
import jakarta.ejb.EJBException;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.service.annotation.EJBBean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Finner EJB hørende til service {@code S} og gjør et kall på den. Konverterer EJB spesifikke exceptions til
 * rammeverk spesifikke exceptions og også bruke i SingleVm mode slik at det blir transparent at kallet ble
 * utført via en EJB.
 * <p/>
 * TODO: Implementer EJB exception konvertering
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class EJBCallProxyHandlerJEE<S> extends EJBCallProxyHandler<S> {
    private final Provider<S> ejbProvider;

    @Inject
    public EJBCallProxyHandlerJEE(@EJBBean Provider<S> ejbProvider) {
        this.ejbProvider = ejbProvider;
    }

    @Override
    public Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        final Object ejb = ejbProvider.get();
        try {
            //noinspection UnnecessaryLocalVariable
            Object result = method.invoke(ejb, args);
            return result;
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new ImplementationException(e);
        } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            if (targetException instanceof EJBException) {
                final Throwable cause = targetException.getCause();
                if (cause != null) {
                    throw cause;
                } else {
                    throw targetException;
                }
            }
            throw targetException;
        }
    }
}
