package no.statkart.skif.exception;

import org.slf4j.Logger;

/**
 * Klasse som representerer implemetasjonsfeil i kode enten på server, eller i kall fra klient.
 *
 * @author Leif Lislegård
 * @since 2.0
 */
public class ImplementationException extends SystemException {
    private static final long serialVersionUID = 1L;

    private void init() {
        //setter standard feilkode og beskrivelse
        setFeilkode("IE000");
        setFeilkodebeskrivelse("Implementasjonsfeil");
    }

    public ImplementationException(String message, Throwable cause, Logger logger) {
        super(message, cause, logger);
        init();
    }

    /**
     * Conventional constructor
     */
    public ImplementationException(String message, Throwable cause) {
        super(message, cause);
        init();
    }


    public ImplementationException(String message) {
        super(message);
    }

    public ImplementationException(String message, Logger logger) {
        super(message, logger);
    }

    public ImplementationException(Throwable cause) {
        this(cause.getLocalizedMessage(), cause);
    }
}
