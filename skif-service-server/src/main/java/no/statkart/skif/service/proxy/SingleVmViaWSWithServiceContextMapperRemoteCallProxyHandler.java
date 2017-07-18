package no.statkart.skif.service.proxy;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import no.statkart.skif.mapper.ExceptionMapping;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.service.LoginUserHolder;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.service.SingleVmRemoteCallContext;
import no.statkart.skif.service.SingleVmServer;
import no.statkart.skif.service.ejb.EJBCallProxyHandler;
import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionContext;

import java.lang.reflect.Method;

/**
 * Kobler seg mot singlevm på tjenersiden på samme måte som {@link SingleVmNoWSWithServiceContextMapperRemoteCallProxyHandler},
 * men tar først veien via mapping.
 *
 * @author Tor Egil R. Strand
 * @since 2.3.0
 */
public class SingleVmViaWSWithServiceContextMapperRemoteCallProxyHandler<S, W extends ServiceWSI> extends SingleVmNoWSWithServiceContextMapperRemoteCallProxyHandler<S> {
    private final Class<S> serviceClass;
    private final Class<W> webServiceClass;
    private final Mapping mapping;
    private final ExceptionMapping exceptionMapping;
    private final SnapshotVersionContext snapshotVersionContext;



    @Inject
    public SingleVmViaWSWithServiceContextMapperRemoteCallProxyHandler(TypeLiteral<S> serviceType, TypeLiteral<W> webServiceType, SingleVmServer singleVmServer, LoginUserHolder loginUserHolder, Provider<ServiceContext> serviceContextProvider, Mapping mapping, ExceptionMapping exceptionMapping, SnapshotVersionContext snapshotVersionContext) {
        super(serviceType, singleVmServer, loginUserHolder, serviceContextProvider, snapshotVersionContext);
        this.exceptionMapping = exceptionMapping;
        this.serviceClass = (Class<S>) serviceType.getRawType();
        this.webServiceClass = (Class<W>) webServiceType.getRawType();
        this.mapping = mapping;
        this.snapshotVersionContext = snapshotVersionContext;
    }

    @Override
    protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        SnapshotVersion snapshotVersion = snapshotVersionContext.getSnapshotVersion();
        // Sett snapshotVersion som skal brukes av mappingrammeverket for inneværende request
        SnapshotVersion orignalSnapshotVersion = snapshotVersionContext.setSnapshotVersion(snapshotVersion);
        serviceRequestScope.enter();
        try {
            serviceRequestScope.seed(SingleVmRemoteCallContext.class, createSingleVmRemoteCallcontext(snapshotVersion));
            final EJBCallProxyHandler<S> ejbCallProxyHandler = singleVmServerEJBProxyHandlerProvider.get();
            final W2DAdapterProxyHandler<W, S> w2d = new W2DAdapterProxyHandler<>(serviceClass, ejbCallProxyHandler, mapping, exceptionMapping);
            final D2WAdapterProxyHandler<S, W> d2w = new D2WAdapterProxyHandler<>(webServiceClass, w2d, mapping, exceptionMapping);

            //noinspection UnnecessaryLocalVariable
            Object result = d2w.invoke(proxy, method, args);

            return result;
        } finally {
            snapshotVersionContext.setSnapshotVersion(orignalSnapshotVersion);
            serviceRequestScope.exit();
        }
    }

    // TODO: context burde også vært gjennom mapperen, men mapperen er ikke fleksibel nok
}
