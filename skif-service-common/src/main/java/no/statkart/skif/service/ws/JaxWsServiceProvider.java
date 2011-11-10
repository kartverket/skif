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
import no.statkart.skif.service.LoginUser;

import javax.annotation.Nullable;
import javax.net.ssl.HostnameVerifier;

/**
 * Guice Provider implementasjon for å opprette en JAX-WS klient for en gitt porttype {@code <T>}. Provideren anvender en
 * JaxWS builder klasse som automatisk avleder Web service endpoint klasse og Web service context path fra porttype klassen.
 *
 * @see JaxWsServiceBuilder
 * @author Henrik Fredholm
 */
public class JaxWsServiceProvider<T> implements Provider<T> {
    private final JaxWsServiceBuilder<T> builder;
    private final LoginUserHolder loginUserHolder;
    private final ServerUrlHolder serverUrlHolder;
    private final HostnameVerifier hostnameVerifier;

    @Inject
    public JaxWsServiceProvider(TypeLiteral<T> type, LoginUserHolder loginUserHolder, ServerUrlHolder serverUrlHolder, @Nullable HostnameVerifier hostnameVerifier) {
        this.loginUserHolder = loginUserHolder;
        this.serverUrlHolder = serverUrlHolder;
        this.hostnameVerifier = hostnameVerifier;
        builder = new JaxWsServiceBuilder<T>((Class<T>) type.getRawType());
        builder.setHostnameVerifier(hostnameVerifier);
    }

    @Override
    public T get() {
        final LoginUser loginUser = loginUserHolder.get();
        if (loginUser ==null) {
            builder.setUsername(null);
            builder.setPassword(null) ;
        } else {
            builder.setUsername(loginUser.getUsername());
            builder.setPassword(loginUser.getPassword()) ;
        }
        builder.setServerUrl(serverUrlHolder.get());
        return builder.build();
    }
}