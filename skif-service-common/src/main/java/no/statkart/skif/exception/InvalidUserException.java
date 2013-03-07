package no.statkart.skif.exception;

import org.slf4j.Logger;

/**
 * Rapporterer at brukernavn og/eller passord ikke er gyldig for pålogging mot SKIF-tjener.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class InvalidUserException extends AccessException {
    private static final long serialVersionUID = 1L;

    public InvalidUserException(String message, Throwable cause, Logger logger) {
        super(message, cause, logger);
    }

    public InvalidUserException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidUserException(String message) {
        super(message);
    }
}
