package no.statkart.skif.service.proxy;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import no.statkart.skif.mapper.ExceptionMapping;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.service.ServiceContextMapper;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionContext;

import javax.annotation.Nullable;
import java.lang.reflect.Method;

/**
 * Proxy som adapterer java interface {@code T} til Web service interface {@code A}. Denne adapter har
 * tilleggsfunksjonalitet som anvender et sett av regler på java intefacemetodens parametre til å bestemme
 * hvilken  {@code SnapshotVersion} som skal gjelde for kallet. Bl.a tillader adapteren at java interface metoden har en
 * ekstra siste parameter som eksplisitt angir {@code SnapshotVersion} for kallet. Denne parameteren overføres
 * som en del av ServiceContext objektet og ikke som en selvstendig parameter i wsapi kallet. Dersom java interfacemetoden
 * ikke har en slik eksplisitt {@code SnapshotVersion} som siste parameter forsøker adapteren istedet å finne en
 * annen parameter i kallet som kan brukes til å bestemme {@code SnapshotVersion}. Hvis adapteren klarer å bestemme
 * {@code SnapshotVersion} settes denne {@code SnapshotVersion} inn i {@code SnapshotVersionContext} før kallet
 * utføres og etter kallet settes opprinnelig verdi på {@code SnapshotVersionContext}. Dersom det ikke er mulig å
 * anvende java interfacemetodens parametre til å bestemme {@code SnapshotVersion} anvendes verdien i
 * {@code SnapshotVersionContext} uforandret.
 *
 * <p>Algoritmen som bestemmer hvilken parameter som skal anvendes for en gitt javaa interfacemetode avhenger kun av
 * java interfacemetodens signatur og ikke av faktiske parameterverdier.
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class D2WAdapterWithServiceContextSVMapperProxyHandler<T, A> extends D2WAdapterWithServiceContextMapperProxyHandler<T, A> {
    final SnapshotVersionArgumentListAnalyser snapshotVersionArgumentListAnalyser = new SnapshotVersionArgumentListAnalyser();
    final SnapshotVersionContext snapshotVersionContext;

    @Inject()
    public D2WAdapterWithServiceContextSVMapperProxyHandler(Provider<A> adapteeProvider, TypeLiteral<A> aType, Mapping map, @Nullable ServiceContextMapper<?> contextMapper, SnapshotVersionContext snapshotVersionContext) {
        this(adapteeProvider, aType, map, contextMapper, null, snapshotVersionContext);
    }

    public D2WAdapterWithServiceContextSVMapperProxyHandler(Provider<A> adapteeProvider, TypeLiteral<A> aType, Mapping map, ServiceContextMapper<?> contextMapper, @Nullable ExceptionMapping exceptionMapping, SnapshotVersionContext snapshotVersionContext) {
        super(adapteeProvider, aType, map, contextMapper, exceptionMapping);
        this.snapshotVersionContext = snapshotVersionContext;
    }

    public D2WAdapterWithServiceContextSVMapperProxyHandler(Class<A> adapteeClass, ProxyHandler<A> handler, Mapping map, ExceptionMapping exceptionMapping, ServiceContextMapper<?> contextMapper, SnapshotVersionContext snapshotVersionContext) {
        super(adapteeClass, handler, map, exceptionMapping, contextMapper);
        this.snapshotVersionContext = snapshotVersionContext;
    }

    protected Object mapArgsAndInvokeMethod(Object proxy, Method method, Object[] args, Method toMethod) throws Throwable {
        SnapshotVersion originalSnapshotVersion = snapshotVersionContext.getSnapshotVersion();
        try {
            SnapshotVersionD2WResult snapshotVersionD2WResult = snapshotVersionArgumentListAnalyser.analyseD2W(method, args);
            SnapshotVersion newSnapshotVersion = snapshotVersionD2WResult.snapshotVersion==null ? originalSnapshotVersion : snapshotVersionD2WResult.snapshotVersion;
            snapshotVersionContext.setSnapshotVersion(newSnapshotVersion);
            Object[] mappedArgs = mapArgs(args, method, toMethod, snapshotVersionD2WResult.lastArg);
            return super.invokeMethodForMappedArgs(proxy, method, toMethod, mappedArgs);
        } finally {
            snapshotVersionContext.setSnapshotVersion(originalSnapshotVersion);
        }
    }
}