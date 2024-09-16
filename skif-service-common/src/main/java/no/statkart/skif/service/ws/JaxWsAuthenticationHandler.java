package no.statkart.skif.service.ws;

import no.statkart.skif.service.HttpRequestAuthenticationOverride;
import no.statkart.skif.service.LoginUser;
import no.statkart.skif.service.LoginUserHolder;

import jakarta.inject.Inject;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.handler.LogicalHandler;
import jakarta.xml.ws.handler.LogicalMessageContext;
import jakarta.xml.ws.handler.MessageContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JaxWsAuthenticationHandler implements LogicalHandler<LogicalMessageContext> {
    private final LoginUserHolder loginUserHolder;
    private final HttpRequestAuthenticationOverride httpRequestHeadersOverride;

    @Inject
    public JaxWsAuthenticationHandler(
            LoginUserHolder loginUserHolder,
            @SuppressWarnings("OptionalUsedAsFieldOrParameterType") // ønsker ikke å injecte null
            Optional<HttpRequestAuthenticationOverride> httpRequestHeadersOverride) {
        this.loginUserHolder = loginUserHolder;
        this.httpRequestHeadersOverride = httpRequestHeadersOverride.orElse(null);
    }

    @Override
    public boolean handleMessage(LogicalMessageContext context) {
        Map<String, List<String>> overrideHeaders = null;
        if (httpRequestHeadersOverride != null) {
            overrideHeaders = httpRequestHeadersOverride.getHttpHeaders();
        }

        if (overrideHeaders != null && overrideHeaders.size() > 0) {
            // Ny logikk til bruk f.eks. ved Bearer Authorization, må sette HTTP header(e) direkte, men ikke overskriv evt andre headere
            @SuppressWarnings("unchecked")
            Map<String, List<String>> headers = (Map<String, List<String>>) context.computeIfAbsent(MessageContext.HTTP_REQUEST_HEADERS, k -> new HashMap<>());
            headers.putAll(overrideHeaders);
        } else {
            LoginUser currentLoginUser = loginUserHolder.get();
            // Gammel logikk som bruker Basic Authentication innebygget i JaxWS
            if (currentLoginUser != null) {
                context.put(BindingProvider.USERNAME_PROPERTY, currentLoginUser.getUsername());
                context.put(BindingProvider.PASSWORD_PROPERTY, currentLoginUser.getPassword());
            } else {
                context.remove(BindingProvider.USERNAME_PROPERTY);
                context.remove(BindingProvider.PASSWORD_PROPERTY);
            }
        }

        return true;
    }

    @Override
    public boolean handleFault(LogicalMessageContext context) {
        return true;
    }

    @Override
    public void close(MessageContext context) {
        // ingenting å gjøre
    }
}
