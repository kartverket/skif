package no.statkart.skif.service.proxy;

import com.google.inject.Inject;
import no.statkart.skif.service.ServiceContextMapper;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.mapper.ExceptionMapping;

import javax.annotation.Nullable;
import java.lang.reflect.Method;

/**
 * Adapter proxy som adapterer domain interface {@code T} til Web service interface {@code A} ved å mappe metoder med
 * samme navn til hverandre og transformere argumentene og resultatet vha et mappingobjekt {@link Mapping}. I tillegg
 * legger adapteren på et {@code ServiceContext} object som siste parameter i Web service kallet som instansieres vha
 * et {@code ServiceContextMapper<?>} objekt.
 * <p/>
 * Adapteren har også exception håndtering dersom denne er tildelt og satt (ikke null).
 * Alle @{Exception}s annotert med {@WebFault} blir mappet over til korresponderende exceptions ihht til mapper.
 * All andre exceptions blir fanget og wrappet til {@link no.statkart.skif.exception.ImplementationException}.
 *
 * @author Henrik Fredholm
 * @NotTheadSafe
 * @since 2.0
 */
public class D2WAdapterWithServiceContextMapperProxyHandler<T, A> extends D2WAdapterProxyHandler<T, A> {
    final ServiceContextMapper<?> contextMapper;

    @Inject()
    public D2WAdapterWithServiceContextMapperProxyHandler(A adaptee, Mapping map, @Nullable ServiceContextMapper<?> contextMapper) {
        this(adaptee, map, contextMapper, null);
    }

    public D2WAdapterWithServiceContextMapperProxyHandler(A adaptee, Mapping map, ServiceContextMapper<?> contextMapper, @Nullable ExceptionMapping exceptionMapping) {
        super(adaptee, map, exceptionMapping);
        this.contextMapper = contextMapper;
    }

    /**
     * Map alle argumenter til Web service objekter. Opprett deretter ApiContext objekt av riktig type og legg på som siste
     * parameter.
     */
    protected Object[] mapArgs(Object[] args, Method m) {
        Object[] mappedArgs = super.mapArgs(args, m);
        if (contextMapper != null) {
            Object wsServiceContext = contextMapper.createWSServiceContextFromDomainServiceContext();
            if (mappedArgs == null) {
                mappedArgs = new Object[]{wsServiceContext};
            } else {
                Object[] mappedArgsWithContext = null;
                mappedArgsWithContext = new Object[mappedArgs.length + 1];
                System.arraycopy(mappedArgs, 0, mappedArgsWithContext, 0, mappedArgs.length);
                mappedArgsWithContext[mappedArgs.length] = wsServiceContext;
                mappedArgs = mappedArgsWithContext;
            }
        }
        return mappedArgs;
    }
}