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
 * Mapping av parametre som kan inneholde et wsapi context objekt som siste parameter og som ikke skal mappes direkte.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class W2DAdapterWithServiceContextMapperProxyHandler<T, A> extends W2DAdapterProxyHandler<T, A> {
    protected final ServiceContextMapper<Object> contextMapper;

    @Inject()
    public W2DAdapterWithServiceContextMapperProxyHandler(@WSServiceChain A adaptee, Mapping map, @Nullable ServiceContextMapper<?> contextMapper) {
        this(adaptee, map, contextMapper, null);
    }

    public W2DAdapterWithServiceContextMapperProxyHandler(A adaptee, Mapping map, ServiceContextMapper<?> contextMapper, @Nullable ExceptionMapping exceptionMapping) {
        super(adaptee, map, exceptionMapping);
        this.contextMapper = (ServiceContextMapper<Object>) contextMapper;
    }

    public W2DAdapterWithServiceContextMapperProxyHandler(Class<A> adapteeClass, ProxyHandler<A> handler, Mapping map, ExceptionMapping exceptionMapping, ServiceContextMapper<?> contextMapper) {
        super(adapteeClass, handler, map, exceptionMapping);
        this.contextMapper = (ServiceContextMapper<Object>) contextMapper;
    }

    /**
     * Mapper argumenter i args slik at de kan brukes som innput parametre til {@code doapiMethod}. Hvis {@code contextMapper}
     * er satt så mappes siste parameter i {@code args} til et intern domain ServiceContext objekt. De resterende parameter
     * mappes på standard vis via {@code map}.
     *
     * @param args argumenter som skal mappes
     * @param wsapiMethod  wsapi metode som ble kallt. Inneholder informasjon om source parameter typer
     * @param doapiMethod  doapi metode som skal kalles. Inneholder informasjon om target parameter typer.
     * @param length antall parametre i [@code args} som skal mappes. Hvis {@code contextMapper} er satt bør verdien være
     *              {@code args.length-1} ellers bør den være {@code args.length} slik at siste parameter også mappes på
     *               standard vis.
     * @return mappet parametre.
     */
    protected Object[] mapArgs(@Nullable Object[] args, Method wsapiMethod, Method doapiMethod, int length) {
        if (contextMapper != null) {
            if (args == null || args.length == 0) {
                throw new ImplementationException(String.format("Method '%s' must have at least one parameter. Is the context parameter missing in the definition of the Web Service method?", wsapiMethod));
            }

            // Contextobjektet må mappes først siden det setter gjeldende snapshotVersion.
            Object wsServiceContext = args[args.length - 1];
            contextMapper.setDomainServiceContextFromWSServiceContext(map, wsServiceContext);
        }
        Object[] mappedArgs = super.mapArgs(args, wsapiMethod, doapiMethod, length);
        return mappedArgs;
    }
}