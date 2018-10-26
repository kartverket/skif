package no.statkart.skif.exception;

import org.slf4j.Logger;

/**
 * Felles exception for feil ved oppslag der datagrunnlag ikke er i henhold til kriterier.
 *
 * @author Leif Lislegård
 * @since 2.0
 */
public class FinderException extends ApplicationException {
    private static final long serialVersionUID = 1L;

    private void init() {
        //setter standard feilkode og beskrivelse
        setFeilkode("FE000");
        setFeilkodebeskrivelse("Objekt ikke funnet");
    }

    protected FinderException(String message, Throwable cause, Logger logger) {
        super(message, cause, logger);
        init();
    }

    /**
     * Conventional constructor
     */
    public FinderException(String message, Throwable cause) {
        super(message, cause);
        init();
    }

    public FinderException(String message) {
        super(message);
        init();
    }
}
