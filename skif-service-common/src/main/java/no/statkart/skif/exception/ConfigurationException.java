package no.statkart.skif.exception;

import org.slf4j.Logger;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class ConfigurationException extends ImplementationException {
    private static final long serialVersionUID = 1L;

    /**
     * Conventional constructor
     */
    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationException(String message, Throwable cause, Logger logger) {
        super(message, cause, logger);
    }

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Logger logger) {
        super(message, logger);
    }

    public ConfigurationException(Throwable cause) {
        super(cause);
    }
}
