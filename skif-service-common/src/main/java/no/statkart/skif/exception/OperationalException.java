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

    private void init() {
        //setter standard feilkode og beskrivelse
        setFeilkode("OE000");
        setFeilkodebeskrivelse("Operasjonell feil");
    }

    protected OperationalException(String message, Throwable cause, Logger logger) {
        super(message, cause, logger);
        init();
    }

    /**
     * Conventional constructor
     */
    public OperationalException(String message, Throwable cause) {
        super(message, cause);
        init();
    }

    public OperationalException(String message) {
        super(message);
        init();
    }

    public OperationalException(String message, Logger logger) {
        super(message, logger);
        init();
    }

    public OperationalException(Throwable throwable) {
        super(throwable);
        init();
    }

}
