package no.statkart.skif.service.proxy;

import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import no.statkart.skif.service.LoginUserHolder;
import no.statkart.skif.service.SingleVmServer;

/**
 * En  {@code ProxyHandler} som i {@code SINGLE_VM}-mode simulerer remote kall fra klient til server {@code singleVmServer}
 * i for service av type {@code <S>}. {@code ProxyHandler}en henter ut en {@code EJBProxyHandler<S>} fra serveren og
 * sender kall videre til denne.
 * <p>
 * Denne klassen tilbyr standard funksjonalitet, dvs spesial håndtering av argumenter.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class SingleVmNoWSRemoteCallProxyHandler<S> extends SingleVmRemoteCallProxyHandler<S> {

    @Inject
    public SingleVmNoWSRemoteCallProxyHandler(TypeLiteral<S> serviceType, SingleVmServer singleVmServer, LoginUserHolder loginUserHolder) {
        super(serviceType, singleVmServer, loginUserHolder);
    }
}
