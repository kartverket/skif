package no.statkart.skif.service.logging;

import java.lang.reflect.Method;

/**
 * Interface for loggrapportering fra en LoggingProxyHandler på tjeneren. Implementasjoner av
 * dette interfacet bestemmer (sammen med logback.xml e.l.) til hvilke logger og på hvilket format det skal logges.
 * Implmentasjonen bindes opp i SKIF. Den kan være singleton.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public interface ServerCallLogger {
    /**
     * Kalles før en EJB blir kalt.
     *
     * @param method metoden som skal kalles
     * @param args   argumentene til metoden som skal kalles
     */
    void logEjbCall(Method method, Object[] args);

    /**
     * Kalles etter at en EJB har returnert uten exception.
     *
     * @param method      metoden som ble kalt og har returnert
     * @param args        argumentene metoden ble kalt med
     * @param returnValue returverdien fra metodecreateCallMessagen som ble kalt
     * @param time        tiden kallet tok, i millisekunder
     */
    void logEjbReturn(Method method, Object[] args, Object returnValue, long time);

    /**
     * Kalles dersom en EJB kastet en exception.
     *
     * @param method metoden som kastet exception
     * @param args   argumentene metoden ble kalt med
     * @param t      exception som ble kastet (kan også være Error)
     * @param time   tiden kallet tok, i millisekunder
     */
    void logEjbError(Method method, Object[] args, Throwable t, long time);

    /**
     * Kalles i det et kall har kommet inn til en web service.
     *
     * @param method metoden som ble kalt
     * @param args   argumentene til metoden som ble kalt
     */
    void logWsCall(Method method, Object[] args);

    /**
     * Kalles i det en web service skal returnere.
     *
     * @param method      metoden som er i ferd med å returnere
     * @param args        argumentene metoden ble kalt med
     * @param returnValue returverdien fra metoden som ble kalt
     * @param time        tiden kallet tok, i millisekunder
     */
    void logWsReturn(Method method, Object[] args, Object returnValue, long time);

    /**
     * Kalles dersom en EJB kastet en exception.
     *
     * @param method metoden som kastet exception
     * @param args   argumentene metoden ble kalt med
     * @param t      exception som ble kastet (kan også være Error)
     * @param time   tiden kallet tok, i millisekunder
     */
    void logWsError(Method method, Object[] args, Throwable t, long time);
}
