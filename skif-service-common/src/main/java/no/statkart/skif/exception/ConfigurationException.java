package no.statkart.skif.exception;

/**
 *
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

    public ConfigurationException(String message) {
        this(message, null);
    }

    public ConfigurationException(Throwable cause) {
        super(cause);
    }
}
