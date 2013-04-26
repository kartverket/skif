package no.statkart.skif.service.logging;

import java.lang.reflect.Method;

/**
 * Interface for loggrapportering fra en LoggingProxyHandler på klienten. Implementasjoner av
 * dette interfacet bestemmer (sammen med logback.xml e.l.) til hvilke logger og på hvilket format det skal logges.
 * Implmentasjonen bindes opp i SKIF. Den kan være singleton.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public interface ClientCallLogger {
    /**
     * Kalles før en service-metode blir kalt.
     *
     * @param callId
     * @param method metoden som skal kalles
     * @param args   argumentene til metoden som skal kalles
     */
    void logClientCall(Long callId, Method method, Object[] args);

    /**
     * Kalles etter at en service-metode har returnert uten exception.
     *
     * @param callId
     * @param method      metoden som ble kalt og har returnert
     * @param args        argumentene metoden ble kalt med
     * @param returnValue returverdien fra metoden som ble kalt
     * @param time        tiden kallet tok, i millisekunder
     */
    void logClientReturn(Long callId, Method method, Object[] args, Object returnValue, long time);

    /**
     * Kalles dersom en service-metode kastet en exception.
     *
     * @param callId
     * @param method metoden som kastet exception
     * @param args   argumentene metoden ble kalt med
     * @param t      exception som ble kastet (kan også være Error)
     * @param time   tiden kallet tok, i millisekunder
     */
    void logClientError(Long callId, Method method, Object[] args, Throwable t, long time);
}
