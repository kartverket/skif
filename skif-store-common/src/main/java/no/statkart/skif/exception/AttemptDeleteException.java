package no.statkart.skif.exception;

import no.statkart.skif.store.BubbleId;

/**
 * Angir at objekt med gitt id ikke kunne slettes
 */
public class AttemptDeleteException extends ApplicationException {
    private static final long serialVersionUID = 1L;

    private BubbleId bubbleId;

    public AttemptDeleteException(BubbleId bubbleId, Throwable e) {
        super(String.valueOf(bubbleId), e);
        this.bubbleId = bubbleId;
    }

    /**
     * Kun for mapping.
     */
    public AttemptDeleteException(BubbleId bubbleId) {
        super(String.valueOf(bubbleId));
        this.bubbleId = bubbleId;
    }

    public BubbleId getBubbleId() {
        return bubbleId;
    }

    public void setBubbleId(BubbleId bubbleId) {
        this.bubbleId = bubbleId;
    }
}
