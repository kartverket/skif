package no.statkart.skif.service.ws;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.InvalidUserException;
import no.statkart.skif.exception.PermissionDeniedException;
import no.statkart.skif.service.LoginUser;
import no.statkart.skif.service.LoginUserHolder;
import no.statkart.skif.service.ServerUrlHolder;
import no.statkart.skif.service.proxy.TerminatingProxyHandler;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.MessageContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Setter innstillinger på web service for hvert kall, siden web servicen-stubben gjenbrukes.
 */
public class JaxWsRequestContextProxyHandler<S> extends TerminatingProxyHandler<S> {
    private final JaxWsServicePool<S> jaxwsPool;
    private final LoginUserHolder loginUserHolder;
    private final ServerUrlHolder serverUrlHolder;
    private final String webServiceContextPath;


    public JaxWsRequestContextProxyHandler(JaxWsServicePool<S> jaxwsPool, LoginUserHolder loginUserHolder, ServerUrlHolder serverUrlHolder, String webServiceContextPath) {
        this.jaxwsPool = jaxwsPool;
        this.loginUserHolder = loginUserHolder;
        this.serverUrlHolder = serverUrlHolder;
        this.webServiceContextPath = webServiceContextPath;
    }

    @Override
    protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        S jaxwsInstance = jaxwsPool.get();

        LoginUser currentLoginUser = loginUserHolder.get();
        try {
            BindingProvider bindings = (BindingProvider) jaxwsInstance;
            if (currentLoginUser != null) {
                bindings.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, currentLoginUser.getUsername());
                bindings.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, currentLoginUser.getPassword());
            } else {
                bindings.getRequestContext().remove(BindingProvider.USERNAME_PROPERTY);
                bindings.getRequestContext().remove(BindingProvider.PASSWORD_PROPERTY);
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
                BindingProvider bindings = (BindingProvider) jaxwsInstance;
                Integer responseCode = (Integer) bindings.getResponseContext().get(MessageContext.HTTP_RESPONSE_CODE);
                if (responseCode != null) {
                    if (responseCode == 401) {
                        throw new InvalidUserException("HTTP 401 Unauthorized for user:"+currentLoginUser, exception);
                    } else if (responseCode == 403) {
                        throw new PermissionDeniedException("HTTP 403 Forbidden for user:"+currentLoginUser, exception);
                    }
                }
            }

            throw exception;
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new ImplementationException(e);
        }
    }
}
