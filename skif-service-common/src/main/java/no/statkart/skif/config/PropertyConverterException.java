package no.statkart.skif.config;

import no.statkart.skif.exception.ConfigurationException;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class PropertyConverterException extends ConfigurationException {
    private static final long serialVersionUID = 1L;

    /**
     * Conventional constructor
     */
    public PropertyConverterException(String message, Throwable cause) {
        super(message, cause);
    }

    public PropertyConverterException(String message) {
        this(message, null);
    }

    public PropertyConverterException(Throwable cause) {
        this(null, cause);
    }
}
