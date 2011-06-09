package no.statkart.skif.exception;

import org.slf4j.Logger;

/**
 * Klasse som representerer feil som stammer ifra servermiljøet.
 *
 * @author Leif Lislegård
 * @since 0.6
 */
public class OperationalException extends SystemException {

    public OperationalException(String message) {
        this(message, null, null);
    }

    public OperationalException(String message, Logger logger) {
        this(message, null, logger);
    }

    public OperationalException(Throwable throwable) {
        this(null, throwable, null);
    }

    public OperationalException(String message, Throwable cause) {
        this(message, cause, null);
    }

    public OperationalException(String message, Throwable cause, Logger logger) {
        super(message, cause, logger);
        setFeilkode("O_1");
        setFeilkodebeskrivelse("Operasjonell feil");
    }


}
