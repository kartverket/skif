package no.statkart.skif.exception;

import no.statkart.skif.exception.ImplementationException;

/**
 *
 * @author Henrik Fredholm
 * @since 0.6
 */
public class ConfigurationException extends ImplementationException {

    public ConfigurationException() {
    }

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationException(Throwable cause) {
        super(cause);
    }
}
