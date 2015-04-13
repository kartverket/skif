package no.statkart.skif.exception;

import no.statkart.skif.store.BubbleId;
import org.slf4j.Logger;

/**
 * Angir at objekt med gitt id ikke finnes. Det kan f.eks være fordi det har blitt slettet.
 *
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @since 2.0
 */
public class ObjectNotFoundException extends ObjectsNotFoundException {
    private static final long serialVersionUID = 1L;

    private final BubbleId<?> notFoundId;

    public ObjectNotFoundException(BubbleId<?> notFoundId) {
        this(notFoundId, null);
    }

    public ObjectNotFoundException(BubbleId<?> notFoundId, Throwable cause) {
        this(notFoundId, cause, null);
    }

    public ObjectNotFoundException(BubbleId<?> notFoundId, Throwable cause, Logger logger) {
        super(notFoundId, cause, logger);
        this.notFoundId = notFoundId;
    }

    public BubbleId<?> getNotFoundId() {
        return notFoundId;
    }
}
