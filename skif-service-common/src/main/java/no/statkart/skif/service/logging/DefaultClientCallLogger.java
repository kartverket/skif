package no.statkart.skif.service.logging;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.exception.ApplicationException;
import no.statkart.skif.service.LoginUserHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * En grei standardimplementasjon av {@link ClientCallLogger} for testing, som eksempel eller som baseklasse.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
@SuppressWarnings("WeakerAccess") // Det skal være mulig å override det meste
@Singleton
public class DefaultClientCallLogger implements ClientCallLogger {
    /**
     * @deprecated Bruk alltid getteren.
     */
    @Deprecated
    private final static Logger logger = LoggerFactory.getLogger(DefaultClientCallLogger.class);

    private final LoginUserHolder loginUserHolder;

    @Inject
    public DefaultClientCallLogger(LoginUserHolder loginUserHolder) {
        this.loginUserHolder = loginUserHolder;
    }

    /**
     * Dels er dette det gamle navnet for {@link #getLogger()} som ligger igjen for kompatibilitet,
     * dels er dette en måte å kunne få tak i {@link #logger} selv om {@code getLogger()} er overridden.
     */
    @SuppressWarnings({"UnusedDeclaration", "deprecation"})
    protected Logger getDefaultLogger() {
        return logger;
    }

    /**
     * @return Loggeren som blir brukt for logging. Override denne for å angi spesielle loggere.
     */
    @SuppressWarnings("deprecation")
    protected Logger getLogger() {
        return logger;
    }

    protected void logCall(Long callId, Method method, Object[] args) {
        CharSequence msg = createCallMessage(callId, method, args);

        getLogger().info(msg.toString());
    }

    /**
     * Lager teksten som skal logges i det en metode blir kalt.
     *
     * @param callId call-id
     * @param method metoden hvis kall skal logges
     * @param args   argumentene til metoden
     * @return teksten som skal logges
     */
    protected CharSequence createCallMessage(Long callId, Method method, Object[] args) {
        StringBuilder buf = new StringBuilder(200)
            .append("Kaller [id=").append(callId).append("] ");

        appendMethod(buf, method, args);

        return buf.append(" [user=").append(loginUserHolder.get().getUsername()).append(']')
            .append(" [").append(Thread.currentThread()).append(']')
            ;
    }

    /**
     * Brukes av standardimplementasjonen av {@link #createCallMessage(Long, java.lang.reflect.Method, Object[])} for å
     * skrive ut hvilken metode som er i ferd med å kalles. Standardimplementasjonen skriver ut
     * &lt;interfacenavn&gt;.&lt;metodenavn&gt;(&lt;...&gt;), hvor
     *
     * @param buf    dit teksten skal appendes
     * @param method metoden hvis kall skal logges
     * @param args   argumentene til metoden
     */
    protected void appendMethod(StringBuilder buf, Method method, Object[] args) {
        buf.append(method.getDeclaringClass().getName())
            .append('.')
            .append(method.getName())
            .append('(');
        appendParameters(buf, method, args);
        buf.append(')');
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

    protected void logReturn(Long callId, Method method, Object[] args, Object returnValue, long time) {
        CharSequence msg = createReturnMessage(callId, method, args, returnValue, time);

        getLogger().info(msg.toString());
    }

    /**
     * Lager teksten som skal logges i det en metode returnerer.
     *
     * @param callId      call-id
     * @param method      metoden hvis retur skal logges
     * @param args        argumentene til metoden
     * @param returnValue verdien metoden returnerte
     * @param time        tiden kallet tok
     * @return teksten som skal logges
     */
    @SuppressWarnings("UnusedParameters")
    protected CharSequence createReturnMessage(Long callId, Method method, Object[] args, Object returnValue, long time) {
        return "<----- [id=" + callId + "] tok " + time + " ms";
    }

    protected void logError(Long callId, Method method, Object[] args, Throwable t, long time) {
        if (t instanceof ApplicationException) {
            CharSequence msg = createApplicationErrorMessage(callId, method, args, t, time);
            if (getLogger().isDebugEnabled()) {
                getLogger().debug(msg.toString(), t);
            } else if (getLogger().isInfoEnabled()) {
                getLogger().info(msg.toString() + " '" + t + '\'');
            }
        } else {
            CharSequence msg = createErrorMessage(callId, method, args, t, time);
            getLogger().error(msg.toString(), t);
        }
    }

    /**
     * Lager teksten som skal logges i det en metode returnerer.
     *
     * @param callId call-id
     * @param method metoden hvis feiling skal logges
     * @param args   argumentene til metoden
     * @param t      den exception metoden kastet (kan også være Error)
     * @param time   tiden kallet tok
     * @return teksten som skal logges
     */
    @SuppressWarnings("UnusedParameters")
    protected CharSequence createErrorMessage(Long callId, Method method, Object[] args, Throwable t, long time) {
        return "<----- [id=" + callId + "] tok " + time + " ms og kastet exception: ";
    }

    /**
     * Lager teksten som skal logges i det en metode returnerer.
     *
     * @param callId call-id
     * @param method metoden hvis feiling skal logges
     * @param args   argumentene til metoden
     * @param t      den exception metoden kastet (kan også være Error)
     * @param time   tiden kallet tok
     * @return teksten som skal logges
     */
    @SuppressWarnings("UnusedParameters")
    protected CharSequence createApplicationErrorMessage(Long callId, Method method, Object[] args, Throwable t, long time) {
        return "<----- [id=" + callId + "] tok " + time + " ms og kastet application exception: ";
    }


    @Override
    public void logClientCall(Long callId, Method method, Object[] args) {
        logCall(callId, method, args);
    }

    @Override
    public void logClientReturn(Long callId, Method method, Object[] args, Object returnValue, long time) {
        logReturn(callId, method, args, returnValue, time);
    }

    @Override
    public void logClientError(Long callId, Method method, Object[] args, Throwable t, long time) {
        logError(callId, method, args, t, time);
    }
}
