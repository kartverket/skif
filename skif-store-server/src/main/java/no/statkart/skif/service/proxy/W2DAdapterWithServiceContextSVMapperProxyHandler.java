package no.statkart.skif.service.proxy;

import com.google.inject.Inject;
import no.statkart.skif.mapper.ExceptionMapping;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.service.ServiceContextMapper;
import no.statkart.skif.service.annotation.WSServiceChain;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionContext;

import javax.annotation.Nullable;
import java.lang.reflect.Method;

/**
 * Proxy som adapterer Web service interface {@code T} til java interface {@code A}. Denne adapter har
 * tilleggsfunksjonalitet som tar høyde for at java intefacemetoden kan ha en ekstra {@code SnapshotVersion} parameter
 * som siste parameter. Hvis dette er tilfellet hentes denne ekstra parameter fra {@code SnapshotVersionContext} som
 * forventes allerede å være satt.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class W2DAdapterWithServiceContextSVMapperProxyHandler<T, A> extends W2DAdapterWithServiceContextMapperProxyHandler<T, A> {
    final SnapshotVersionArgumentListAnalyser snapshotVersionArgumentListAnalyser = new SnapshotVersionArgumentListAnalyser();
    final SnapshotVersionContext snapshotVersionContext;

    @Inject()
    public W2DAdapterWithServiceContextSVMapperProxyHandler(@WSServiceChain A adaptee, Mapping map, @Nullable ServiceContextMapper<?> contextMapper, SnapshotVersionContext snapshotVersionContext) {
        super(adaptee, map,contextMapper);
        this.snapshotVersionContext=snapshotVersionContext;
    }

    public W2DAdapterWithServiceContextSVMapperProxyHandler(Class<A> adapteeClass, ProxyHandler<A> handler, Mapping map, ExceptionMapping exceptionMapping, ServiceContextMapper<?> contextMapper, SnapshotVersionContext snapshotVersionContext) {
        super(adapteeClass, handler, map, exceptionMapping, contextMapper);
        this.snapshotVersionContext = snapshotVersionContext;
    }

    protected Object mapArgsAndInvokeMethod(Object proxy, Method method, Object[] args, Method toMethod) throws Throwable {
        SnapshotVersion originalSnapshotVersion = snapshotVersionContext.getSnapshotVersion();
        try {
            SnapshotVersionW2DResult snapshotVersionW2DResult = snapshotVersionArgumentListAnalyser.analyseW2D(toMethod);
            Object[] mappedArgs = mapArgs(args, method, toMethod, snapshotVersionW2DResult.lastArg);
            if (snapshotVersionW2DResult.mustAddSnapshotVersionAsLastArgument){
                mappedArgs[snapshotVersionW2DResult.lastArg]=snapshotVersionContext.getSnapshotVersion();
            }
            return invokeMethodForMappedArgs(proxy, method, toMethod, mappedArgs);
        } finally {
            snapshotVersionContext.setSnapshotVersion(originalSnapshotVersion);
        }
    }
}