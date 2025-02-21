package no.statkart.skif.mapper;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.PermissionDeniedException;
import no.statkart.skif.exception.SkifException;

import java.lang.reflect.Method;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class AbstractExceptionMapper<M extends ExceptionMapping> extends AbstractMapper<M> {
    protected final boolean wrapD2WRuntimeExceptions;

    public AbstractExceptionMapper(Class<? extends M> mappingClass, boolean wrapD2WRuntimeExceptions) {
        super(mappingClass);
        this.wrapD2WRuntimeExceptions = wrapD2WRuntimeExceptions;
    }

    /**
     * Wrap ukjente RuntimeExceptions i en ImplementationException. EJBAccessException er et unntak, som skal gjøres om til PermissionDeniedException.
     *
     * @param args    argumentene til mapping-funksjonen
     * @return instans av mappet klasse
     */
    @Override
    protected Object d2w(Method method, Object[] args) {
        if (args.length == 1 && !(args[0] instanceof SkifException)) {
            Throwable t = (Throwable) args[0];

            // Kan ikke bruke instanceof på EJBAccessException, for den klassen finnes ikke på klientsiden, kun tjenersiden (og singlevm)
            if (t.getClass().getName().equals("jakarta.ejb.EJBAccessException")) {
                PermissionDeniedException e = new PermissionDeniedException(t.getMessage(), t);
                e.setStackTrace(t.getStackTrace());
                return super.d2w(method, new Object[]{e});
            } else {
                ImplementationException e = new ImplementationException(t.getMessage(), t);
                e.setStackTrace(t.getStackTrace());
                return super.d2w(method, new Object[]{e});
            }
        } else {
            return super.d2w(method, args);
        }
    }

}
