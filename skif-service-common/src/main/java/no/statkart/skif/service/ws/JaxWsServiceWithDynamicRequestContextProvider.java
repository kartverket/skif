package no.statkart.skif.service.ws;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import no.statkart.skif.service.LoginUserHolder;
import no.statkart.skif.service.ServerUrlHolder;

import javax.annotation.Nullable;
import javax.net.ssl.HostnameVerifier;

/**
 * Guice Provider implementasjon for å opprette en JAX-WS klient for en gitt porttype {@code <T>}. Provideren anvender en
 * JaxWS builder klasse som automatisk avleder Web service endpoint klasse og Web service context path fra porttype klassen.
 *
 * @see no.statkart.skif.service.ws.JaxWsServiceBuilder
 * @author Henrik Fredholm
 */
public class JaxWsServiceWithDynamicRequestContextProvider<S> implements Provider<S> {
    private final TypeLiteral<S> type;
    private final JaxWsServiceBuilder<S> builder;
    private final LoginUserHolder loginUserHolder;
    private final ServerUrlHolder serverUrlHolder;
    private final HostnameVerifier hostnameVerifier;

    @Inject
    public JaxWsServiceWithDynamicRequestContextProvider(TypeLiteral<S> type, LoginUserHolder loginUserHolder, ServerUrlHolder serverUrlHolder, @Nullable HostnameVerifier hostnameVerifier) {
        this.type = type;
        this.loginUserHolder = loginUserHolder;
        this.serverUrlHolder = serverUrlHolder;
        this.hostnameVerifier = hostnameVerifier;
        builder = new JaxWsServiceBuilder<S>((Class<S>) type.getRawType());
        builder.setHostnameVerifier(hostnameVerifier);
    }

    @Override
    public S get() {
        final String webServiceContextPath = builder.getWebServiceContextPath();
        final S jaxWsInstance = builder.build();
        final JaxWsRequestContextProxyHandler<S> jaxWsRequestContextProxyHandler = new JaxWsRequestContextProxyHandler<S>(jaxWsInstance, loginUserHolder, serverUrlHolder, webServiceContextPath);
        final S jaxWsInstanceWithProxy = jaxWsRequestContextProxyHandler.buildProxy(type);
        return jaxWsInstanceWithProxy;
    }
}