package no.statkart.skif.config;

import no.statkart.skif.exception.ConfigurationException;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public class PropertyConverterException extends ConfigurationException {
    public PropertyConverterException() {
    }

    public PropertyConverterException(String message) {
        super(message);
    }

    public PropertyConverterException(String message, Throwable cause) {
        super(message, cause);
    }

    public PropertyConverterException(Throwable cause) {
        super(cause);
    }
}
