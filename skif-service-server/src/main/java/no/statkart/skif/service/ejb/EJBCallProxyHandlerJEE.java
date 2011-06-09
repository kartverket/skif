package no.statkart.skif.service.ejb;

import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.NotImplementedException;

import javax.ejb.EJBException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Finner EJB hørende til service {@code S} og gjør et kall på den. Konverterer EJB spesifikke exceptions til
 * rammeverk spesifikke exceptions og også bruke i SingleVm mode slik at det blir transparent at kallet ble
 * utført via en EJB.
 *
 * TODO: Implementer EJB exception konvertering
 *
 * @author Henrik Fredholm
 * @since 1.1
 */
public class EJBCallProxyHandlerJEE<S> extends EJBCallProxyHandler<S> {
    private final TypeLiteral<S> type;

    @Inject
    public EJBCallProxyHandlerJEE(TypeLiteral<S> type) {
        this.type = type;
    }

    @Override
    public Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        final Object ejb = EJBLookupHelper.getInstance().lookupEjb(type.getRawType());
        try {
            Object result = method.invoke(ejb, args);
            return result;
        } catch (IllegalAccessException e) {
            throw new ImplementationException(e);
        } catch (IllegalArgumentException e) {
            throw new ImplementationException(e);
        } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            if (targetException instanceof EJBException) {
                final Throwable cause = ((EJBException) targetException).getCause();
                throw cause;
            }
            throw targetException;
        }
    }
}
