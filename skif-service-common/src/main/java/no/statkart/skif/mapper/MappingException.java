package no.statkart.skif.mapper;

import no.statkart.skif.exception.ImplementationException;

/**
 * Exceptions som kan forekomme under mapping2 prosessen
 *
 * @author Henrik Fredholm
 */
public class MappingException extends ImplementationException {

    public MappingException(Throwable cause) {
        this(null, cause);
    }

    public MappingException(String message) {
        this(message, null);
    }

    public MappingException(String message, Throwable cause) {
        super(message, cause);
    }
}