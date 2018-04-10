package no.statkart.skif.mapper;

import no.statkart.skif.exception.ImplementationException;

/**
 * Exceptions som kan forekomme under mapping prosessen
 *
 * @author Henrik Fredholm
 */
public class MappingException extends ImplementationException {
    private static final long serialVersionUID = 1L;

    /**
     * Conventional constructor
     */
    public MappingException(String message, Throwable cause) {
        super(message, cause);
    }

    public MappingException(String message) {
        super(message);
    }

    public MappingException(Throwable cause) {
        super(cause);
    }
}