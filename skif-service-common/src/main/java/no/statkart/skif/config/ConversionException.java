package no.statkart.skif.config;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public class ConversionException extends ConfigurationRuntimeException {
    /**
     * Constructs a new <code>ConversionException</code> without specified
     * detail message.
     */
    public ConversionException()
    {
        super();
    }

    /**
     * Constructs a new <code>ConversionException</code> with specified
     * detail message.
     *
     * @param message  the error message
     */
    public ConversionException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>ConversionException</code> with specified
     * nested <code>Throwable</code>.
     *
     * @param cause  the exception or error that caused this exception to be thrown
     */
    public ConversionException(Throwable cause)
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
    public ConversionException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
