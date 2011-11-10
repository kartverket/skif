package no.statkart.skif.exception;

import org.slf4j.Logger;

/**
 * @author Oddbjørn Kvalsund
 * @since 2.0
 */
public class ValidationException extends ApplicationException {

    protected ValidationException(String message, Throwable cause, Logger logger) {
        super(message, cause, logger);

        //setter standard feilkode og beskrivelse
        setFeilkode("VE000");
        setFeilkodebeskrivelse("Valideringsfeil");
    }

    /**
     * Conventional constructor
     */
    public ValidationException(String message, Throwable cause) {
        this(message, cause, null);
    }

    public ValidationException(String message) {
        this(message, null);
    }
}