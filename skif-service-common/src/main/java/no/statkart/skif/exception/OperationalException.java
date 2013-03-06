package no.statkart.skif.exception;

import org.slf4j.Logger;

/**
 * Klasse som representerer feil som stammer ifra servermiljøet.
 *
 * @author Leif Lislegård
 * @since 2.0
 */
public class OperationalException extends SystemException {
    private static final long serialVersionUID = 1L;

    protected OperationalException(String message, Throwable cause, Logger logger) {
        super(message, cause, logger);

        //setter standard feilkode og beskrivelse
        setFeilkode("OE000");
        setFeilkodebeskrivelse("Operasjonell feil");
    }

    /**
     * Conventional constructor
     */
    public OperationalException(String message, Throwable cause) {
        this(message, cause, null);
    }




    public OperationalException(String message) {
        this(message, null, null);
    }

    public OperationalException(String message, Logger logger) {
        this(message, null, logger);
    }

    public OperationalException(Throwable throwable) {
        this(null, throwable, null);
    }




}
