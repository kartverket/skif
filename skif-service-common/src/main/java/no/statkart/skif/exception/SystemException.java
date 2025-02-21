package no.statkart.skif.exception;

import org.slf4j.Logger;

/**
 * Felles type som beskriver systemfeil.
 *
 * Systemfeil er feil som ikke er {@link ApplicationException applikasjonsfeil} - med andre ord feil som ikke er
 * applikasjonslogiske og som stammer ifra systemet ellers.
 *
 * En skiller disse inn i {@link ImplementationException implementasjonsfeil} og {@link OperationalException operasjonsfeil}.
 *
 * @author Leif Lislegård
 * @since 2.0
 */
public class SystemException extends SkifException {
    private static final long serialVersionUID = 1L;

    protected SystemException(String message, Throwable cause, Logger logger) {
        super(message, cause, logger);
    }

    /**
     * Conventional constructor
     */
    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }



    public SystemException(String message, Logger logger) {
        super(message, logger);
    }

    public SystemException(String message) {
        super(message);
    }

    public SystemException(Throwable throwable) {
        super(null, throwable);
    }



}
