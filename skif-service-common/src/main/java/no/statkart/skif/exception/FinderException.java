package no.statkart.skif.exception;

import org.slf4j.Logger;

/**
 * Felles exception for feil ved oppslag der datagrunnlag ikke er i henhold til kriterier.
 *
 * @author Leif Lislegård
 * @since 0.6
 */
public class FinderException extends ApplicationException {

    protected FinderException(String message, Throwable cause, Logger logger) {
        super(message, cause, logger);

        //setter standard feilkode og beskrivelse
        setFeilkode("FE000");
        setFeilkodebeskrivelse("Objekt ikke funnet");
    }

    /**
     * Conventional constructor
     */
    public FinderException(String message, Throwable cause) {
        this(message, cause, null);
    }

    public FinderException(String message) {
        this(message, null);
    }
}
