package no.statkart.skif.exception;

/**
 * Exception som kan kastes der funksjonalitet ikke er implementert.
 *
 * @author Leif Lislegård
 * @since 2.0
 */
public class NotImplementedException extends ImplementationException {
    private static final long serialVersionUID = 1L;

    /**
     * Conventional constructor
     */
    public NotImplementedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotImplementedException(String message) {
        super(message);
    }

    public NotImplementedException() {
        this("Functionality not implemented");
    }
}
