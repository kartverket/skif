package no.statkart.skif.exception;

import no.statkart.skif.store.BubbleId;
import org.slf4j.Logger;

import java.util.Collections;

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
        super(Collections.singleton(notFoundId));
        this.notFoundId = notFoundId;
    }

    public ObjectNotFoundException(BubbleId<?> notFoundId, Throwable cause) {
        super(Collections.singleton(notFoundId), cause);
        this.notFoundId = notFoundId;
    }

    public ObjectNotFoundException(BubbleId<?> notFoundId, Throwable cause, Logger logger) {
        super(Collections.singleton(notFoundId), cause, logger);
        this.notFoundId = notFoundId;
    }

    public BubbleId<?> getNotFoundId() {
        return notFoundId;
    }
}
