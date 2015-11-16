package no.statkart.skif.service.proxy;

import com.google.inject.Inject;
import no.statkart.skif.service.logging.ServerCallLogger;

import java.lang.reflect.Method;

/**
 * Proxyledd for å logge kall til web service. Hvordan det skal logges bestemmes av implementasjonen av
 * {@link no.statkart.skif.service.logging.ServerCallLogger} som benyttes.
 * <p>
 * Denne proxyen skal ligge i ws-kjeden. Det betyr at denne proxyen kalles inne i metoden WSBean, slik at kallet til
 * den og alt før det allerede har skjedd. Kall som blir avvist pga. ugyldig brukernavn og/eller passord vil ikke bli
 * logget, da de bli avvist lenger ut.
 *
 * @author Tor Egil R. Strand
 * @author 2.2.0
 */
public class WsLoggingProxyHandler<S> extends ChainedProxyHandler<S> {
    private final ServerCallLogger serverCallLogger;

    @Inject
    public WsLoggingProxyHandler(ServerCallLogger serverCallLogger) {
        this.serverCallLogger = serverCallLogger;
    }

    @Override
    protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        serverCallLogger.logWsCall(method, args);
        final long startTime = System.currentTimeMillis();
        try {
            Object returnValue = chained.invoke(proxy, method, args);
            final long endTime = System.currentTimeMillis();
            serverCallLogger.logWsReturn(method, args, returnValue, endTime - startTime);
            return returnValue;
        } catch (Throwable t) {
            final long endTime = System.currentTimeMillis();
            serverCallLogger.logWsError(method, args, t, endTime - startTime);
            throw t;
        }
    }
}
