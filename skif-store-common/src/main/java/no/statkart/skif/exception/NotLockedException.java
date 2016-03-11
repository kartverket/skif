package no.statkart.skif.exception;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class NotLockedException extends OperationalException {
    private static final long serialVersionUID = 1L;

    public NotLockedException(String message) {
        super(message);
    }
}
