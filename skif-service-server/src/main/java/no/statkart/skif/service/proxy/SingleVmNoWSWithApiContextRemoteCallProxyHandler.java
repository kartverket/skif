package no.statkart.skif.service.proxy;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.service.SingleVmRemoteCallContext;
import no.statkart.skif.service.SingleVmServer;
import no.statkart.skif.service.LoginUserHolder;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class SingleVmNoWSWithApiContextRemoteCallProxyHandler<S> extends SingleVmRemoteCallProxyHandler<S> {
    protected final Provider<ServiceContext> serviceContextProvider;
    @Inject
    public SingleVmNoWSWithApiContextRemoteCallProxyHandler(TypeLiteral<S> serviceType, SingleVmServer singleVmServer, LoginUserHolder loginUserHolder, Provider<ServiceContext> serviceContextProvider) {
        super(serviceType, singleVmServer, loginUserHolder);
        this.serviceContextProvider = serviceContextProvider;
    }

    @Override
    protected SingleVmRemoteCallContext createSingleVmRemoteCallcontext() {
        final SingleVmRemoteCallContext singleVmRemoteCallcontext = super.createSingleVmRemoteCallcontext();
        // TODO: Bruke mapping2 til å mappe context
        singleVmRemoteCallcontext.getContextData().put("serviceContext", serviceContextProvider.get());
        return singleVmRemoteCallcontext;
    }
}
