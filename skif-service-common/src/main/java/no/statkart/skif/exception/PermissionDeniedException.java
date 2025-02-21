package no.statkart.skif.exception;

import org.slf4j.Logger;

/**
 * Rapporterer at en gyldig bruker har prøvd utføre en ulovlig tjenesteoperasjon.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class PermissionDeniedException extends AccessException {
    private static final long serialVersionUID = 1L;

    public PermissionDeniedException(String message, Throwable cause, Logger logger) {
        super(message, cause, logger);
    }

    public PermissionDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

    public PermissionDeniedException(String message) {
        super(message);
    }
}
