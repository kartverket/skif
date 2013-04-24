package no.statkart.skif.service.proxy;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.service.logging.ServerCallLogger;

import java.lang.reflect.Method;

/**
 * Proxyledd for å logge kall til EJB. Hvordan det skal logges bestemmes av implementasjonen av {@link ServerCallLogger}
 * som benyttes.
 *
 * @author Tor Egil R. Strand
 * @author 2.2.0
 */
public class EjbLoggingProxyHandler<S> extends ChainedProxyHandler<S> {
    private final ServerCallLogger serverCallLogger;

    @Inject
    public EjbLoggingProxyHandler(ServerCallLogger serverCallLogger) {
        this.serverCallLogger = serverCallLogger;
    }

    @Override
    protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        serverCallLogger.logEjbCall(method, args);
        final long startTime = System.currentTimeMillis();
        try {
            Object returnValue = chained.invoke(proxy, method, args);
            final long endTime = System.currentTimeMillis();
            serverCallLogger.logEjbReturn(method, args, returnValue, endTime - startTime);
            return returnValue;
        } catch (Throwable t) {
            final long endTime = System.currentTimeMillis();
            serverCallLogger.logEjbError(method, args, t, endTime - startTime);
            throw t;
        }
    }
}
