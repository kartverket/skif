package no.statkart.skif.service.ws;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.service.proxy.TerminatingProxyHandler;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.WebServiceException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.time.Duration;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;

class JaxWsRequestInvokeProxyHandler<S> extends TerminatingProxyHandler<S> {
    private static final Logger logger = LoggerFactory.getLogger(JaxWsRequestInvokeProxyHandler.class);
    private final GenericObjectPool<S> portPool;
    private final Provider<WebServiceExceptionMapper> jaxWsExceptionHandlerProvider;

    JaxWsRequestInvokeProxyHandler(GenericObjectPool<S> portPool, Provider<WebServiceExceptionMapper> jaxWsExceptionHandlerProvider) {
        this.portPool = portPool;
        this.jaxWsExceptionHandlerProvider = jaxWsExceptionHandlerProvider;
    }

    @Override
    protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        S client;
        try {
            client = portPool.borrowObject(Duration.ZERO);
        } catch (NoSuchElementException e) {
            if (portPool.getBlockWhenExhausted()) {
                logger.warn("No instances available, consider increasing pool size for port instances, blocking...");
                client = portPool.borrowObject();
            } else {
                throw e;
            }
        }
        try {
            return method.invoke(client, args);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof WebServiceException) {
                Optional<Throwable> remappedOption;
                try {
                    remappedOption = jaxWsExceptionHandlerProvider
                            .get()
                            .mapException(
                                    (WebServiceException) cause,
                                    ((BindingProvider) client).getResponseContext(),
                                    method,
                                    args);
                } catch (Throwable t) {
                    cause.addSuppressed(t);
                    throw t;
                }
                if (remappedOption.isPresent()) {
                    throw remappedOption.get();
                }
            }

            if (cause != null) {
                throw cause;
            } else {
                throw e;
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new ImplementationException(e);
        } finally {
            portPool.returnObject(client);
        }
    }
}
