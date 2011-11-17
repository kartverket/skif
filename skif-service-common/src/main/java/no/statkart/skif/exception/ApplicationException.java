package no.statkart.skif.exception;

import org.slf4j.Logger;

/**
 * Felles klasse for alle typer applikasjonsfeil.
 *
 * @author Leif Lislegård
 * @since 2.0
 */
public class ApplicationException extends SkifException {

    protected ApplicationException(String message, Throwable cause, Logger logger) {
        super(message, cause, logger);
    }

    /**
     * Conventional constructor
     */
    public ApplicationException(String message, Throwable cause) {
        this(message, cause, null);
    }

    public ApplicationException(String message) {
        super(message);
    }
}
