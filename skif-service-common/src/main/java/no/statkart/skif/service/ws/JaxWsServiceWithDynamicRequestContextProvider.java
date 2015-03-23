package no.statkart.skif.service.ws;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import no.statkart.skif.service.LoginUserHolder;
import no.statkart.skif.service.ServerUrlHolder;

import javax.annotation.Nullable;
import javax.net.ssl.HostnameVerifier;

/**
 * Guice Provider implementasjon for å opprette en JAX-WS klient for en gitt porttype {@code <T>}. Provideren anvender en
 * JaxWS builder klasse som automatisk avleder Web service endpoint klasse og Web service context path fra porttype klassen.
 *
 * @see JaxWsServicePool
 */
@Singleton
public class JaxWsServiceWithDynamicRequestContextProvider<S> implements Provider<S> {
    private final TypeLiteral<S> type;
    private final JaxWsServicePool<S> pool;
    private final LoginUserHolder loginUserHolder;
    private final ServerUrlHolder serverUrlHolder;

    @Inject
    public JaxWsServiceWithDynamicRequestContextProvider(TypeLiteral<S> type, LoginUserHolder loginUserHolder, ServerUrlHolder serverUrlHolder, @Nullable HostnameVerifier hostnameVerifier) {
        this.type = type;
        this.loginUserHolder = loginUserHolder;
        this.serverUrlHolder = serverUrlHolder;
        pool = new JaxWsServicePool<S>((Class<S>) type.getRawType());
        pool.setHostnameVerifier(hostnameVerifier);
    }

    @Override
    public S get() {
        final String webServiceContextPath = pool.getWebServiceContextPath();
        final JaxWsRequestContextProxyHandler<S> jaxWsRequestContextProxyHandler = new JaxWsRequestContextProxyHandler<S>(pool, loginUserHolder, serverUrlHolder, webServiceContextPath);
        final S jaxWsInstanceWithProxy = jaxWsRequestContextProxyHandler.buildProxy(type);
        return jaxWsInstanceWithProxy;
    }
}