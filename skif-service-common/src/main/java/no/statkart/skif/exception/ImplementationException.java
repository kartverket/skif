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

    public ImplementationException(String message, Throwable cause, Logger logger) {
        super(message, cause, logger);

        //setter standard feilkode og beskrivelse
        setFeilkode("IE000");
        setFeilkodebeskrivelse("Implementasjonsfeil");
    }

    /**
     * Conventional constructor
     */
    public ImplementationException(String message, Throwable cause) {
        this(message, cause, null);
    }


    public ImplementationException(String message) {
        this(message, null, null);
    }

    public ImplementationException(String message, Logger logger) {
        this(message, null, logger);
    }

    public ImplementationException(Throwable cause) {
        this(cause.getLocalizedMessage(), cause);
    }
}
