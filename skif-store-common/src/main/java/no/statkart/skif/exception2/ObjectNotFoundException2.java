package no.statkart.skif.exception2;

import no.statkart.skif.exception.FinderException;
import no.statkart.skif.store2.BubbleId2;

/**
 * Angir at objekt med gitt id ikke finnes. Det kan f.eks være fordi det har blitt slettet.
 *
 * TODO: Er det riktig den buker BubbleId2? Bør den flyttes til pakke store.excption
 */
public class ObjectNotFoundException2 extends FinderException {
    private BubbleId2 notFoundId;

    public ObjectNotFoundException2(BubbleId2 notFoundId) {
        super(String.valueOf(notFoundId));
        this.notFoundId = notFoundId;
    }

    public ObjectNotFoundException2(BubbleId2 notFoundId, String message) {
        super(message);
        this.notFoundId = notFoundId;
    }

    public BubbleId2 getNotFoundId() {
        return notFoundId;
    }
}
