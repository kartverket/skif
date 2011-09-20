package no.statkart.skif.exception;

import no.statkart.skif.store.BubbleId;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class NotLockedException extends OperationalException {

    public NotLockedException(String message) {
        super(message);
    }
}
