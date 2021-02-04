package no.statkart.skif.service.ws;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.InvalidUserException;
import no.statkart.skif.exception.PermissionDeniedException;
import no.statkart.skif.service.HttpRequestAuthenticationOverride;
import no.statkart.skif.service.LoginUser;
import no.statkart.skif.service.LoginUserHolder;
import no.statkart.skif.service.ServerUrlHolder;
import no.statkart.skif.service.proxy.TerminatingProxyHandler;

import javax.annotation.Nullable;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.MessageContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Setter innstillinger på web service for hvert kall, siden web servicen-stubben gjenbrukes.
 */
public class JaxWsRequestContextProxyHandler<S> extends TerminatingProxyHandler<S> {
    private final JaxWsServicePool<S> jaxwsPool;
    private final LoginUserHolder loginUserHolder;
    private final ServerUrlHolder serverUrlHolder;
    private final HttpRequestAuthenticationOverride httpRequestHeadersOverride;
    private final String webServiceContextPath;

    public JaxWsRequestContextProxyHandler(JaxWsServicePool<S> jaxwsPool,
                                           LoginUserHolder loginUserHolder,
                                           ServerUrlHolder serverUrlHolder,
                                           @Nullable HttpRequestAuthenticationOverride httpRequestHeadersOverride,
                                           String webServiceContextPath) {
        this.jaxwsPool = jaxwsPool;
        this.loginUserHolder = loginUserHolder;
        this.serverUrlHolder = serverUrlHolder;
        this.httpRequestHeadersOverride = httpRequestHeadersOverride;
        this.webServiceContextPath = webServiceContextPath;
    }

    @Override
    protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        S jaxwsInstance = jaxwsPool.get();
        LoginUser currentLoginUser = loginUserHolder.get();
        BindingProvider bindings = (BindingProvider) jaxwsInstance;

        try {
            Map<String, List<String>> overrideHeaders = null;
            if (httpRequestHeadersOverride != null) {
                overrideHeaders = httpRequestHeadersOverride.getHttpHeaders();
            }

            if (overrideHeaders != null && overrideHeaders.size() > 0) {
                // Ny logikk til bruk f.eks. ved Bearer Authorization, må sette HTTP header(e) direkte, men ikke overskriv evt andre headere
                @SuppressWarnings("unchecked")
                Map<String, List<String>> headers = (Map<String, List<String>>)bindings.getRequestContext().computeIfAbsent(MessageContext.HTTP_REQUEST_HEADERS, k -> new HashMap<>());
                headers.putAll(overrideHeaders);
            } else {
                // Gammel logikk som bruker Basic Authentication innebygget i JaxWS
                if (currentLoginUser != null) {
                    bindings.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, currentLoginUser.getUsername());
                    bindings.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, currentLoginUser.getPassword());
                } else {
                    bindings.getRequestContext().remove(BindingProvider.USERNAME_PROPERTY);
                    bindings.getRequestContext().remove(BindingProvider.PASSWORD_PROPERTY);
                }
            }

            String currentServerUrl = serverUrlHolder.get();
            String serviceEndpointUrl = currentServerUrl + webServiceContextPath;
            bindings.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceEndpointUrl);

            return method.invoke(jaxwsInstance, args);
        } catch (InvocationTargetException e) {
            Throwable exception = e.getTargetException();

            // JAX-WS har ingen direkte måte å si at HTTP BASIC autentisering mislykkes (det er utenfor SOAP standarden).
            // Det kastes en intern exceptiontype, men den varierer ut fra implementasjonen. Teksten i den kan jo også
            // endre seg. Sjekker derfor HTTP-statuskoden direkte dersom det kastes en exception i det hele tatt.
            if (exception instanceof WebServiceException) {
                Integer responseCode = (Integer) bindings.getResponseContext().get(MessageContext.HTTP_RESPONSE_CODE);
                if (responseCode != null) {
                    String username = currentLoginUser == null ? "<null>" : currentLoginUser.getUsername();
                    Object endpoint = bindings.getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
                    if (responseCode == 401) {
                        throw new InvalidUserException("HTTP 401 Unauthorized from " + endpoint + " for user " + username, exception);
                    } else if (responseCode == 403) {
                        throw new PermissionDeniedException("HTTP 403 Forbidden from " + endpoint + " for user " + username, exception);
                    }
                    if (exception.getCause() instanceof ConnectException) {
                        throw new ImplementationException("Could not connect to endpoint '" + endpoint + "' with user '" + username + "'", exception);
                    }
                }
            }

            throw exception;
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new ImplementationException(e);
        }
    }
}
