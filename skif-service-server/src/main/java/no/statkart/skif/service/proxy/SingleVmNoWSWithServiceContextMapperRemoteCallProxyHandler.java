package no.statkart.skif.service.proxy;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.SkifException;
import no.statkart.skif.service.LoginUserHolder;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.service.SingleVmRemoteCallContext;
import no.statkart.skif.service.SingleVmServer;
import no.statkart.skif.service.ejb.EJBCallProxyHandler;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionContext;
import no.statkart.skif.util.CopyHelper;

import java.lang.reflect.Method;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class SingleVmNoWSWithServiceContextMapperRemoteCallProxyHandler<S> extends SingleVmRemoteCallProxyHandler<S> {
    protected final Provider<ServiceContext> serviceContextProvider;
    protected final SnapshotVersionContext snapshotVersionContext;

    @Inject
    public SingleVmNoWSWithServiceContextMapperRemoteCallProxyHandler(TypeLiteral<S> serviceType, SingleVmServer singleVmServer, LoginUserHolder loginUserHolder, Provider<ServiceContext> serviceContextProvider, SnapshotVersionContext snapshotVersionContext) {
        super(serviceType, singleVmServer, loginUserHolder);
        this.serviceContextProvider = serviceContextProvider;
        this.snapshotVersionContext = snapshotVersionContext;
    }

    protected SingleVmRemoteCallContext createSingleVmRemoteCallcontext(SnapshotVersion snapshotVersion) {
        final SingleVmRemoteCallContext singleVmRemoteCallcontext = super.createSingleVmRemoteCallcontext();
        ServiceContext serviceContextCopy = CopyHelper.copy(serviceContextProvider.get());
        singleVmRemoteCallcontext.getContextData().put("serviceContext", serviceContextCopy);
        singleVmRemoteCallcontext.getContextData().put("snapshotVersion", snapshotVersion);
        return singleVmRemoteCallcontext;
    }

    @Override
    protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        SnapshotVersion snapshotVersion = snapshotVersionContext.getSnapshotVersion();
        return invokeMethodWithSnapshot(proxy, method, args, snapshotVersion);

    }

    protected Object invokeMethodWithSnapshot(Object proxy, Method method, Object[] args, SnapshotVersion snapshotVersion) throws Throwable {
        // Sett snapshotVersion som skal brukes av mappingrammeverket for inneværende request
        serviceRequestScope.enter();
        SnapshotVersion orignalSnapshotVersion = snapshotVersionContext.setSnapshotVersion(snapshotVersion); // Denne feiler aldrig
        try {
            serviceRequestScope.seed(SingleVmRemoteCallContext.class, createSingleVmRemoteCallcontext(snapshotVersion));
            final EJBCallProxyHandler<S> ejbCallProxyHandler = singleVmServerEJBProxyHandlerProvider.get();
            args = copyArgs(args, snapshotVersion);
            Object result = ejbCallProxyHandler.invoke(proxy, method, args);
            return CopyHelper.copy(result);
        } catch (Exception e) {
            if (e instanceof SkifException || e.getClass().getName().equals("no.statkart.skif.util.testsupport.SkifServerTestCaseTestException")) {
                throw e;
            } else {
                throw new ImplementationException(e.getMessage(), e);
            }
        } finally {
            snapshotVersionContext.setSnapshotVersion(orignalSnapshotVersion);
            serviceRequestScope.exit();
        }
    }

    private Object[] copyArgs(Object[] args, SnapshotVersion snapshotVersion) {
        if (args!=null && args.length==1 && args[0] instanceof RunOnServerMethod) {
            return args;
        } else {
            return CopyHelper.copy(args, snapshotVersion);
        }
    }
}
