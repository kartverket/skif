package no.statkart.skif.service.logging;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import no.statkart.skif.service.ServiceRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * En grei standardimplementasjon av {@link ServerCallLogger} for testing, som eksempel eller som baseklasse.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
@Singleton
public class DefaultServerCallLogger implements ServerCallLogger {
    private final static Logger logger = LoggerFactory.getLogger(DefaultServerCallLogger.class);

    private final Provider<ServiceRequestContext> serviceRequestContextProvider;

    @Inject
    public DefaultServerCallLogger(Provider<ServiceRequestContext> serviceRequestContextProvider) {
        this.serviceRequestContextProvider = serviceRequestContextProvider;
    }

    @SuppressWarnings("UnusedParameters")
    protected void logCall(Method method, Object[] args) {
        ServiceRequestContext serviceRequestContext = serviceRequestContextProvider.get();

        StringBuilder buf = new StringBuilder(200);
        buf.append("Kaller [id=");
        buf.append(serviceRequestContext.getCallId());
        buf.append("] ");
        buf.append(method.getDeclaringClass().getName());
        buf.append(".");
        buf.append(method.getName());
//        buf.append(argsInfo);
        buf.append(" [user=").append(serviceRequestContext.getCallerPrincipal().getName()).append("]");
        buf.append(" [").append(Thread.currentThread()).append("]");

        logger.info(buf.toString());
    }

    @SuppressWarnings("UnusedParameters")
    protected void logReturn(Method method, Object[] args, Object returnValue, long time) {
        ServiceRequestContext serviceRequestContext = serviceRequestContextProvider.get();

        StringBuilder buf = new StringBuilder(200);
        buf.append("<----- [id=");
        buf.append(serviceRequestContext.getCallId());
        buf.append("] tok ");
        buf.append(time);

        logger.info(buf.toString());
    }

    @SuppressWarnings("UnusedParameters")
    protected void logError(Method method, Object[] args, Throwable t, long time) {
        ServiceRequestContext serviceRequestContext = serviceRequestContextProvider.get();

        StringBuilder buf = new StringBuilder(200);
        buf.append("<----- [id=");
        buf.append(serviceRequestContext.getCallId());
        buf.append("] tok ");
        buf.append(time);

        buf.append(" og kastet exception: ");

        logger.error(buf.toString(), t);
    }

    @Override
    public void logEjbCall(Method method, Object[] args) {
        logCall(method, args);
    }

    @Override
    public void logEjbReturn(Method method, Object[] args, Object returnValue, long time) {
        logReturn(method, args, returnValue, time);
    }

    @Override
    public void logEjbError(Method method, Object[] args, Throwable t, long time) {
        logError(method, args, t, time);
    }

    @Override
    public void logWsCall(Method method, Object[] args) {
        logCall(method, args);
    }

    @Override
    public void logWsReturn(Method method, Object[] args, Object returnValue, long time) {
        logReturn(method, args, returnValue, time);
    }

    @Override
    public void logWsError(Method method, Object[] args, Throwable t, long time) {
        logError(method, args, t, time);
    }
}
