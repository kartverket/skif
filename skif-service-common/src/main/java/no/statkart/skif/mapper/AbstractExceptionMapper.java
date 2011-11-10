package no.statkart.skif.mapper;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.SkifException;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class AbstractExceptionMapper extends AbstractMapper {
    protected final boolean wrapD2WRuntimeExceptions;

    public AbstractExceptionMapper(Class<? extends Mapping> mappingClass, boolean wrapD2WRuntimeExceptions) {
        super(mappingClass);
        this.wrapD2WRuntimeExceptions = wrapD2WRuntimeExceptions;
    }

    public AbstractExceptionMapper(Class<? extends Mapping> mappingClass, ObjectFactory wsapiObjectFactory, ObjectFactory domainObjectFactory, boolean mergeMapping, boolean wrapD2WRuntimeExceptions) {
        super(mappingClass, wsapiObjectFactory, domainObjectFactory, mergeMapping);
        this.wrapD2WRuntimeExceptions = wrapD2WRuntimeExceptions;
    }

    /**
     * Wrap ukjendte RuntimeExceptions i en ImplementationException.
     * @param args
     * @return
     */
    @Override
    protected Object d2w(Object[] args) {
        if (args.length==1 && !(args[0] instanceof SkifException)) {
            Throwable t = (Throwable) args[0];
            ImplementationException e = new ImplementationException(t.getMessage(), t);
            e.setStackTrace(t.getStackTrace());
            return super.d2w(new Object[]{e});
        } else {
            return super.d2w(args);    //To change body of overridden methods use File | Settings | File Templates.
        }
    }

}
