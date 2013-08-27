package no.statkart.skif.storetest.domain.endringslogg;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.storetest.domain.basic.BubbleWithRelationId;

/**
 * Endring for {@link no.statkart.skif.storetest.domain.basic.BubbleWithRelation}.
 *
 * @author Tor Egil R. Strand
 * @since 2.3.0
 */
public class BubbleWithRelationEndring<I extends BubbleWithRelationId<?>> extends Endring<I> {
    private static final long serialVersionUID = 1L;

    private BubbleWithRelationId<?> bubbleWithRelationId;

    @Override
    public BubbleWithRelationEndringId<?> getId() {
        return (BubbleWithRelationEndringId<?>) super.getId();
    }

    @Override
    public BubbleWithRelationId<?> getEndretBubbleId() {
        return getBubbleWithRelationId();
    }

    @Override
    protected void setEndretBubbleIdImpl(BubbleId<?> id) {
        setBubbleWithRelationId((BubbleWithRelationId<?>) id);
    }

    public BubbleWithRelationId<?> getBubbleWithRelationId() {
        return bubbleWithRelationId;
    }

    void setBubbleWithRelationId(BubbleWithRelationId<?> bubbleWithRelationId) {
        this.bubbleWithRelationId = bubbleWithRelationId;
    }
}
