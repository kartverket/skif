package no.statkart.skif.mapper;

import no.statkart.skif.exception.ImplementationException;

/**
 * Exceptions som kan forekomme under mapping prosessen
 *
 * @author Henrik Fredholm
 */
public class MappingException extends ImplementationException {

    /**
     * Conventional constructor
     */
    public MappingException(String message, Throwable cause) {
        super(message, cause);
    }

    public MappingException(String message) {
        this(message, null);
    }

    public MappingException(Throwable cause) {
        super(cause);
    }
}