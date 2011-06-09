package no.statkart.skif.exception;

import org.slf4j.Logger;

/**
 * Klasse som representerer implemetasjonsfeil i kode enten på server, eller i kall fra klient.
 *
 * @author Leif Lislegård
 * @since 0.6
 */
public class ImplementationException extends SystemException {

    public ImplementationException() {
        this(null, null, null);
    }

    public ImplementationException(String message) {
        this(message, null, null);
    }

    public ImplementationException(String message, Logger logger) {
        this(message, null, logger);
    }

    public ImplementationException(Throwable throwable) {
        this(null, throwable, null);
    }

    public ImplementationException(String message, Throwable cause) {
        this(message, cause, null);
    }

    public ImplementationException(String message, Throwable cause, Logger logger) {
        super(message, cause, logger);
        setFeilkode("I_1");
        setFeilkodebeskrivelse("Implementasjonsfeil");
    }
}
