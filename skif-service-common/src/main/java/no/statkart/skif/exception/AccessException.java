package no.statkart.skif.exception;

import org.slf4j.Logger;

/**
 * Baseklasse for alle tilgangsproblemer.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class AccessException extends ApplicationException {
    private static final long serialVersionUID = 1L;

    public AccessException(String message, Throwable cause, Logger logger) {
        super(message, cause, logger);
    }

    public AccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccessException(String message) {
        super(message);
    }
}
