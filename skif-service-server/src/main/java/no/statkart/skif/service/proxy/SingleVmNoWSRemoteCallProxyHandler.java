package no.statkart.skif.service.proxy;

import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import no.statkart.skif.service.LoginUserHolder;
import no.statkart.skif.service.SingleVmServer;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class SingleVmNoWSRemoteCallProxyHandler<S> extends SingleVmRemoteCallProxyHandler<S> {

    @Inject
    public SingleVmNoWSRemoteCallProxyHandler(TypeLiteral<S> serviceType, SingleVmServer singleVmServer, LoginUserHolder loginUserHolder) {
        super(serviceType, singleVmServer, loginUserHolder);
    }
}
