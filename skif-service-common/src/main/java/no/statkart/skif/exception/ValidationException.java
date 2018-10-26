package no.statkart.skif.exception;

import org.slf4j.Logger;

/**
 * @author Oddbjørn Kvalsund
 * @since 2.0
 */
public class ValidationException extends ApplicationException {
    private static final long serialVersionUID = 1L;

    private void init() {
        //setter standard feilkode og beskrivelse
        setFeilkode("VE000");
        setFeilkodebeskrivelse("Valideringsfeil");
    }

    protected ValidationException(String message, Throwable cause, Logger logger) {
        super(message, cause, logger);
        init();
    }

    /**
     * Conventional constructor
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
        init();
    }

    public ValidationException(String message) {
        super(message);
        init();
    }
}