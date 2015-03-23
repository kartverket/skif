package no.statkart.skif.service.ws;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.service.LoginUserHolder;
import no.statkart.skif.service.ServerUrlHolder;
import no.statkart.skif.service.LoginUser;
import no.statkart.skif.service.proxy.TerminatingProxyHandler;

import javax.xml.ws.BindingProvider;
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

        try {
            BindingProvider bindings = (BindingProvider) jaxwsInstance;
            LoginUser currentLoginUser = loginUserHolder.get();
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
            throw e.getTargetException();
        } catch (IllegalArgumentException e) {
            throw new ImplementationException(e);
        } catch (IllegalAccessException e) {
            throw new ImplementationException(e);
        }
    }
}
