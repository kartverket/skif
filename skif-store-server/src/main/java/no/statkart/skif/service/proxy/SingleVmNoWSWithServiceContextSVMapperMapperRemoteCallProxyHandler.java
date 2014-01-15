package no.statkart.skif.service.proxy;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import no.statkart.skif.service.LoginUserHolder;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.service.SingleVmServer;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionContext;

import java.lang.reflect.Method;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author
 * @since 2.4
 */
public class SingleVmNoWSWithServiceContextSVMapperMapperRemoteCallProxyHandler<S> extends SingleVmNoWSWithServiceContextMapperRemoteCallProxyHandler<S> {
    private SnapshotVersionArgumentListAnalyser snapshotVersionArgumentListAnalyser = new SnapshotVersionArgumentListAnalyser();

    @Inject
    public SingleVmNoWSWithServiceContextSVMapperMapperRemoteCallProxyHandler(TypeLiteral<S> serviceType, SingleVmServer singleVmServer, LoginUserHolder loginUserHolder, Provider<ServiceContext> serviceContextProvider, SnapshotVersionContext snapshotVersionContext) {
        super(serviceType, singleVmServer, loginUserHolder, serviceContextProvider, snapshotVersionContext);
    }


    @Override
    protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        SnapshotVersionD2WResult snapshotVersionD2WResult = snapshotVersionArgumentListAnalyser.analyseD2W(method, args);
        SnapshotVersion snapshotVersion = snapshotVersionD2WResult.snapshotVersion;
        if (snapshotVersion==null) {
            snapshotVersion = snapshotVersionContext.getSnapshotVersion();
        }
        return invokeMethodWithSnapshot(proxy, method, args, snapshotVersion);
    }
}
