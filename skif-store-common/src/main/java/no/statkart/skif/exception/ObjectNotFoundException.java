package no.statkart.skif.exception;

import no.statkart.skif.store.BubbleId;

/**
 * Angir at objekt med gitt id ikke finnes. Det kan f.eks være fordi det har blitt slettet.
 *
 * TODO: Er det riktig den buker BubbleId? Bør den flyttes til pakke store.excption
 */
public class ObjectNotFoundException extends FinderException {
    private BubbleId notFoundId;

    public ObjectNotFoundException(BubbleId notFoundId) {
        super(String.valueOf(notFoundId));
        this.notFoundId = notFoundId;
    }

    public ObjectNotFoundException(BubbleId notFoundId, String message) {
        super(message);
        this.notFoundId = notFoundId;
    }

    public BubbleId getNotFoundId() {
        return notFoundId;
    }
}
