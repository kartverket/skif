package no.statkart.skif.service.proxy;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.service.annotation.CallId;
import no.statkart.skif.service.logging.ClientCallLogger;

import java.lang.reflect.Method;

/**
 * Proxyledd for å logge kall til EJB. Hvordan det skal logges bestemmes av implementasjonen av {@link ClientCallLogger}
 * som benyttes.
 *
 * @author Tor Egil R. Strand
 * @author 2.2.0
 */
public class ClientLoggingProxyHandler<S> extends ChainedProxyHandler<S> {
    private final ClientCallLogger clientCallLogger;
    private final Provider<Long> callIdProvider;

    @Inject
    public ClientLoggingProxyHandler(ClientCallLogger clientCallLogger, @CallId Provider<Long> callIdProvider) {
        this.clientCallLogger = clientCallLogger;
        this.callIdProvider = callIdProvider;
    }

    @Override
    protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        final Long callId = callIdProvider.get();

        clientCallLogger.logClientCall(callId, method, args);
        final long startTime = System.currentTimeMillis();
        try {
            Object returnValue = chained.invoke(proxy, method, args);
            final long endTime = System.currentTimeMillis();
            clientCallLogger.logClientReturn(callId, method, args, returnValue, endTime - startTime);
            return returnValue;
        } catch (Throwable t) {
            final long endTime = System.currentTimeMillis();
            clientCallLogger.logClientError(callId, method, args, t, endTime - startTime);
            throw t;
        }
    }
}
