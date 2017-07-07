package no.statkart.skif.service.proxy;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import no.statkart.skif.mapper.ExceptionMapping;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.service.*;
import no.statkart.skif.service.ejb.EJBCallProxyHandler;
import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionContext;

import javax.annotation.Nullable;
import java.lang.reflect.Method;

/**
 * SingleVmViaWSWithServiceContextSVMapperRemoteCallProxyHandler
 * SingleVmViaWSWithContextAndSnapshotRemoteCallProxyHandler
 * @since 2.4
 */
public class SingleVmViaWSWithServiceContextSVMapperRemoteCallProxyHandler<S, W extends ServiceWSI> extends SingleVmNoWSWithServiceContextMapperRemoteCallProxyHandler<S> {
    private final Class<S> serviceClass;
    private final Class<W> webServiceClass;
    private final Mapping mapping;
    private final ExceptionMapping exceptionMapping;
    private final SnapshotVersionContext snapshotVersionContext;
    private final SnapshotVersionArgumentListAnalyser snapshotVersionArgumentListAnalyser = new SnapshotVersionArgumentListAnalyser();
    private final ServiceContextMapper<?> serviceContextMapper;

    @Inject
    public SingleVmViaWSWithServiceContextSVMapperRemoteCallProxyHandler(TypeLiteral<S> serviceType, TypeLiteral<W> webServiceType, SingleVmServer singleVmServer, LoginUserHolder loginUserHolder, Provider<ServiceContext> serviceContextProvider, Mapping mapping, ExceptionMapping exceptionMapping, SnapshotVersionContext snapshotVersionContext, @Nullable ServiceContextMapper<?> serviceContextMapper) {
        super(serviceType, singleVmServer, loginUserHolder, serviceContextProvider, snapshotVersionContext);
        this.exceptionMapping = exceptionMapping;
        this.serviceContextMapper = serviceContextMapper;
        this.serviceClass = (Class<S>) serviceType.getRawType();
        this.webServiceClass = (Class<W>) webServiceType.getRawType();
        this.mapping = mapping;
        this.snapshotVersionContext = snapshotVersionContext;
    }

    protected SingleVmRemoteCallContext createSingleVmRemoteCallcontext(SnapshotVersion snapshotVersion) {
        final SingleVmRemoteCallContext singleVmRemoteCallcontext = super.createSingleVmRemoteCallcontext();
        singleVmRemoteCallcontext.getContextData().put("snapshotVersion", snapshotVersionContext.getSnapshotVersion());
        return singleVmRemoteCallcontext;
    }


    @Override
    protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        SnapshotVersionD2WResult snapshotVersionD2WResult = snapshotVersionArgumentListAnalyser.analyseD2W(method, args);
        SnapshotVersion snapshotVersion = snapshotVersionD2WResult.snapshotVersion;
        if (snapshotVersion==null) {
            snapshotVersion = snapshotVersionContext.getSnapshotVersion();
        }

        serviceRequestScope.enter();
        SnapshotVersion orignalSnapshotVersion = snapshotVersionContext.setSnapshotVersion(snapshotVersion);
        try {
            serviceRequestScope.seed(SingleVmRemoteCallContext.class, createSingleVmRemoteCallcontext(snapshotVersion));

            final EJBCallProxyHandler<S> ejbCallProxyHandler = singleVmServerEJBProxyHandlerProvider.get();
            final W2DAdapterProxyHandler<W, S> w2d = new W2DAdapterWithServiceContextSVMapperProxyHandler<>(serviceClass, ejbCallProxyHandler, mapping, exceptionMapping, singleVmServer.getInjector().getInstance(serviceContextMapper.getClass()), snapshotVersionContext);
            final D2WAdapterProxyHandler<S, W> d2w = new D2WAdapterWithServiceContextSVMapperProxyHandler<>(webServiceClass, w2d, mapping, exceptionMapping, serviceContextMapper, snapshotVersionContext);

            //noinspection UnnecessaryLocalVariable
            Object result = d2w.invoke(proxy, method, args);

            return result;
        } finally {
            snapshotVersionContext.setSnapshotVersion(orignalSnapshotVersion);
            serviceRequestScope.exit();
        }
    }

}
