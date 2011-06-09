package no.statkart.skif.exception;

/**
 * Exception som kan kastes for ikke (ennå) implementert funksjonalitet
 *
 * @author Leif Lislegård
 * @since 0.6
 */
public class NotImplementedException extends ImplementationException {

    public NotImplementedException() {
        this("Funksjonalitet ikke ferdig implementert!");
    }

    public NotImplementedException(String message) {
        super(message);
    }
}
