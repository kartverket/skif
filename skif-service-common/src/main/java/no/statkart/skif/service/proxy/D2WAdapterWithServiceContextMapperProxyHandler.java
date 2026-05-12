package no.statkart.skif.service.proxy;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import jakarta.annotation.Nullable;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.mapper.ExceptionMapping;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.service.ServiceContextMapper;

import java.lang.reflect.Method;

/**
 * Proxy som adapterer java interface {@code T} til Web service interface {@code A} ved å mappe metoder med
 * samme navn til hverandre og transformere argumentene og resultatet vha et mappingobjekt {@link Mapping}.
 * I tillegg legger adapteren på et wsapi {@code ServiceContext} object som siste parameter i Web service kallet.
 * Adapteren har et {@code ServiceContextMapper<?>} objekt som brukes til å lage {@code ServiceContext}
 * objektet. slik at relevant context state blir lagt inn {@code ServiceContext}.
 * <p>
 * Adapteren har også exception håndtering dersom denne er tildelt og satt (ikke null).
 * Alle {@link Exception}s annotert med {@link jakarta.xml.ws.WebFault} blir mappet over til korresponderende exceptions ihht til mapper.
 * All andre exceptions blir fanget og wrappet til {@link no.statkart.skif.exception.ImplementationException}.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class D2WAdapterWithServiceContextMapperProxyHandler<T, A> extends D2WAdapterProxyHandler<T, A> {
    private final ServiceContextMapper<?> contextMapper;

    @Inject()
    public D2WAdapterWithServiceContextMapperProxyHandler(Provider<A> adapteeProvider, TypeLiteral<A> aType, Mapping map, @Nullable ServiceContextMapper<?> contextMapper) {
        this(adapteeProvider, aType, map, contextMapper, null);
    }

    public D2WAdapterWithServiceContextMapperProxyHandler(Provider<A> adapteeProvider, TypeLiteral<A> aType, Mapping map, ServiceContextMapper<?> contextMapper, @Nullable ExceptionMapping exceptionMapping) {
        super(adapteeProvider, aType, map, exceptionMapping);
        this.contextMapper = contextMapper;
    }

    public D2WAdapterWithServiceContextMapperProxyHandler(Class<A> adapteeClass, ProxyHandler<A> handler, Mapping map, ExceptionMapping exceptionMapping, ServiceContextMapper<?> contextMapper) {
        super(adapteeClass, handler, map, exceptionMapping);
        this.contextMapper = contextMapper;
    }

    protected ServiceContextMapper<?> getContextMapper() {
        return contextMapper;
    }

    /**
     * Map alle argumenter til Web service objekter. Opprett deretter ServiceContext objekt av riktig type og legg på som siste
     * parameter i wsapi kall
     */
    protected Object[] mapArgs(Object[] args, Method fromMethod, Method toMethod, int length) {
        Object[] mappedArgs;
        if (contextMapper != null) {
            mappedArgs = super.mapArgs(args, fromMethod, toMethod, length);
            if (length+1!=mappedArgs.length) {
                throw new ImplementationException(String.format("Wong number of method arguments for mapping '%s' to '%s'. Is the context parameter missing in the definition of the Web Service method?", fromMethod,toMethod));
            }
            mappedArgs[length] = contextMapper.createWSServiceContextFromDomainServiceContext(map);
        } else {
            int argsLength = (args==null)? 0 : args.length;
            if (argsLength!=length) {
                throw new ImplementationException(String.format("Wong number of method arguments for mapping '%s' to '%s'. If the Web Service method has an additional context parameter, then a ContextMapper needs to be set for the service", fromMethod,toMethod));
            }
            mappedArgs = super.mapArgs(args, fromMethod, toMethod, length);
        }
        return mappedArgs;
    }
}
