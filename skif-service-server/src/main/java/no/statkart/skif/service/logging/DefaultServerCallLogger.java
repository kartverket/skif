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

    @SuppressWarnings("UnusedDeclaration")
    protected Provider<ServiceRequestContext> getServiceRequestContextProvider() {
        return serviceRequestContextProvider;
    }

    @SuppressWarnings("UnusedDeclaration")
    protected Logger getDefaultLogger() {
        return logger;
    }

    protected void logCall(Method method, Object[] args) {
        CharSequence msg = createCallMessage(method, args);

        logger.info(msg.toString());
    }

    /**
     * Lager teksten som skal logges i det en metode blir kalt.
     *
     * @param method metoden hvis kall skal logges
     * @param args   argumentene til metoden
     * @return teksten som skal logges
     */
    protected CharSequence createCallMessage(Method method, Object[] args) {
        ServiceRequestContext serviceRequestContext = serviceRequestContextProvider.get();

        StringBuilder buf = new StringBuilder(200);
        buf.append("Kaller [id=");
        buf.append(serviceRequestContext.getCallId());
        buf.append(", parent=");
        buf.append(serviceRequestContext.getParentCallId());
        buf.append("] ");
        appendMethod(buf, method, args);
        buf.append(" [user=").append(serviceRequestContext.getCallerPrincipal().getName()).append("]");
        buf.append(" [").append(Thread.currentThread()).append("]");
        return buf;
    }

    /**
     * Brukes av standardimplementasjonen av {@link #createCallMessage(java.lang.reflect.Method, Object[])} for å
     * skrive ut hvilken metode som er i ferd med å kalles. Standardimplementasjonen skriver ut
     * &lt;interfacenavn&gt;.&lt;metodenavn&gt;(&lt;...&gt;), hvor
     *
     * @param buf    dit teksten skal appendes
     * @param method metoden hvis kall skal logges
     * @param args   argumentene til metoden
     */
    protected void appendMethod(StringBuilder buf, Method method, Object[] args) {
        buf.append(method.getDeclaringClass().getName());
        buf.append(".");
        buf.append(method.getName());
        buf.append("(");
        appendParameters(buf, method, args);
        buf.append(")");
    }

    /**
     * Benyttes av standardimplmentasjonen av {@link #appendMethod(StringBuilder, java.lang.reflect.Method, Object[])}
     * for å legge på parameterlisten. Standardimplementasjonen returnerer bare en kommaseparert liste med typenavn,
     * siden inneholdet i argumentene kan være uhensiksmessig stort.
     *
     * @param buf    dit teksten skal appendes
     * @param method metoden hvis kall skal logges
     * @param args   argumentene til metoden
     */
    @SuppressWarnings("UnusedParameters")
    protected void appendParameters(StringBuilder buf, Method method, Object[] args) {
        boolean comma = false;
        for (Class<?> parameterType : method.getParameterTypes()) {
            if (comma) {
                buf.append(", ");
            }
            comma = true;
            buf.append(parameterType.getName());
        }
    }

    protected void logReturn(Method method, Object[] args, Object returnValue, long time) {
        CharSequence msg = createReturnMessage(method, args, returnValue, time);

        logger.info(msg.toString());
    }

    /**
     * Lager teksten som skal logges i det en metode returnerer.
     *
     * @param method      metoden hvis retur skal logges
     * @param args        argumentene til metoden
     * @param returnValue verdien metoden returnerte
     * @param time        tiden kallet tok
     * @return teksten som skal logges
     */
    @SuppressWarnings("UnusedParameters")
    protected CharSequence createReturnMessage(Method method, Object[] args, Object returnValue, long time) {
        ServiceRequestContext serviceRequestContext = serviceRequestContextProvider.get();

        StringBuilder buf = new StringBuilder(200);
        buf.append("<----- [id=");
        buf.append(serviceRequestContext.getCallId());
        buf.append("] tok ");
        buf.append(time);
        buf.append(" ms");
        return buf;
    }

    protected void logError(Method method, Object[] args, Throwable t, long time) {
        CharSequence msg = createErrorMessage(method, args, t, time);

        logger.error(msg.toString(), t);
    }

    /**
     * Lager teksten som skal logges i det en metode returnerer.
     *
     * @param method metoden hvis feiling skal logges
     * @param args   argumentene til metoden
     * @param t      den exception metoden kastet (kan også være Error)
     * @param time   tiden kallet tok
     * @return teksten som skal logges
     */
    @SuppressWarnings("UnusedParameters")
    protected CharSequence createErrorMessage(Method method, Object[] args, Throwable t, long time) {
        ServiceRequestContext serviceRequestContext = serviceRequestContextProvider.get();

        StringBuilder buf = new StringBuilder(200);
        buf.append("<----- [id=");
        buf.append(serviceRequestContext.getCallId());
        buf.append("] tok ");
        buf.append(time);
        buf.append(" ms og kastet exception: ");
        return buf;
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
