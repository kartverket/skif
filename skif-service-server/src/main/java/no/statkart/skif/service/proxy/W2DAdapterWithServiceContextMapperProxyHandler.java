package no.statkart.skif.service.proxy;

import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.mapper.ExceptionMapping;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.service.ServiceContextMapper;
import no.statkart.skif.service.annotation.WSServiceChain;

import javax.annotation.Nullable;
import java.lang.reflect.Method;

/**
 * @author Henrik Fredholm
 * @NotTheadSafe
 * @since 1.1
 */
public class W2DAdapterWithServiceContextMapperProxyHandler<T, A> extends W2DAdapterProxyHandler<T, A> {
    final ServiceContextMapper<Object> contextMapper;

    @Inject()
    public W2DAdapterWithServiceContextMapperProxyHandler(@WSServiceChain A adaptee, Mapping map, @Nullable ServiceContextMapper<?> contextMapper) {
        this(adaptee, map, contextMapper, null);
    }

    public W2DAdapterWithServiceContextMapperProxyHandler(A adaptee, Mapping map, ServiceContextMapper<?> contextMapper, @Nullable ExceptionMapping exceptionMapping) {
        super(adaptee, map, exceptionMapping);
        this.contextMapper = (ServiceContextMapper<Object>) contextMapper;
    }

    /**
     * Map først siste parameter fra Api Context objekt til intern ServiceContext2 objekt. Map deretter resterende
     * argumenter og returner disse.
     * <p/>
     * Kaster ImplementationException dersom siste parameter ikke er grunnbokcontext
     */
    protected Object[] mapArgs(Object[] args, Method m) {
        if (contextMapper != null) {
            if (args.length == 0) {
                //Ingen GrunnbokContext. Denne er påkrevd så vi kaster exception
                throw new ImplementationException("ServiceContext objekt mangler i parameterlisten for kallet!");
            }
            Object wsServiceContext = args[args.length - 1];
            contextMapper.setDomainServiceContextFromWSServiceContext(wsServiceContext);
            Object[] argsWithoutContext = new Object[args.length - 1];
            System.arraycopy(args, 0, argsWithoutContext, 0, argsWithoutContext.length);
            Object[] mappedArgsWithoutContext = super.mapArgs(argsWithoutContext, m);
            return mappedArgsWithoutContext;
        } else {
            return super.mapArgs(args, m);
        }
    }
}