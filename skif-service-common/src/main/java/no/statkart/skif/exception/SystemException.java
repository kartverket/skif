package no.statkart.skif.exception;

import org.slf4j.Logger;

/**
 * Felles klasse som beskriver systemfeil.
 *
 * Systemfeil er feil som ikke er {@link ApplicationException applikasjonsfeil} -  feil som stammer ifra andre ting enn
 * applikasjonsspesifike feilsituasjoner.
 *
 * @author Leif Lislegård
 * @since 0.6
 */
public class SystemException extends SkifException {

    protected SystemException() {
    }

    public SystemException(String message) {
        super(message);
    }

    public SystemException(String message, Logger logger) {
        super(message);
        if (logger != null)
            logger.error(message);
    }

    public SystemException(Throwable throwable) {
        this(null, throwable);
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }

    public SystemException(String message, Throwable cause, Logger logger) {
        super(message, cause);
        if (logger != null)
            logger.error(message, cause);
    }

}
