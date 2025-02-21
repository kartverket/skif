package no.statkart.skif.service.proxy;

import com.google.inject.Inject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionContext;

import java.lang.reflect.Method;

/**
 * ProxyHandler som setter SnapshotVersionContext ut fra parametrene slik {@link D2WAdapterWithServiceContextSVMapperProxyHandler}
 * gjør. Denne er ment å brukes i call-chain på tjeneren.
 *
 * @since 2.9.0
 */
public class SnapshotVersionProxyHandler<S> extends ChainedProxyHandler<S> {
    private final SnapshotVersionContext snapshotVersionContext;

    @Inject
    public SnapshotVersionProxyHandler(SnapshotVersionContext snapshotVersionContext) {
        this.snapshotVersionContext = snapshotVersionContext;
    }

    @Override
    protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        final SnapshotVersionArgumentListAnalyser snapshotVersionArgumentListAnalyser = new SnapshotVersionArgumentListAnalyser();
        SnapshotVersion originalSnapshotVersion = snapshotVersionContext.getSnapshotVersion();
        try {
            SnapshotVersionD2WResult snapshotVersionD2WResult = snapshotVersionArgumentListAnalyser.analyseD2W(method, args);
            SnapshotVersion newSnapshotVersion = snapshotVersionD2WResult.snapshotVersion == null ? originalSnapshotVersion : snapshotVersionD2WResult.snapshotVersion;
            snapshotVersionContext.setSnapshotVersion(newSnapshotVersion);
            return chained.invoke(proxy, method, args);
        } finally {
            snapshotVersionContext.setSnapshotVersion(originalSnapshotVersion);
        }
    }
}
