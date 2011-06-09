package no.statkart.skif.config;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public class ConfigurationRuntimeException extends RuntimeException {
    /**
     * Constructs a new <code>ConversionException</code> without specified
     * detail message.
     */
    public ConfigurationRuntimeException()
    {
        super();
    }

    /**
     * Constructs a new <code>ConversionException</code> with specified
     * detail message.
     *
     * @param message  the error message
     */
    public ConfigurationRuntimeException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>ConversionException</code> with specified
     * nested <code>Throwable</code>.
     *
     * @param cause  the exception or error that caused this exception to be thrown
     */
    public ConfigurationRuntimeException(Throwable cause)
    {
        super(cause);
    }

    /**
     * Constructs a new <code>ConversionException</code> with specified
     * detail message and nested <code>Throwable</code>.
     *
     * @param message  the error message
     * @param cause    the exception or error that caused this exception to be thrown
     */
    public ConfigurationRuntimeException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
