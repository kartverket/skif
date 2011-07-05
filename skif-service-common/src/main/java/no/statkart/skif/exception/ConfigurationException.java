package no.statkart.skif.exception;

/**
 *
 * @author Henrik Fredholm
 * @since 0.6
 */
public class ConfigurationException extends ImplementationException {


    /**
     * Conventional constructor
     */
    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationException(String message) {
        this(message, null);
    }

    public ConfigurationException(Throwable cause) {
        super(cause);
    }
}
