package no.statkart.skif.storetest.domain.endringslogg;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.storetest.domain.basic.SubTypedBubbleId;

/**
 * Endring for {@link no.statkart.skif.storetest.domain.basic.SubTypedBubble}.
 *
 * @author Tor Egil R. Strand
 * @since 2.3.0
 */
public class SubTypedBubbleEndring<I extends SubTypedBubbleId<?>> extends Endring<I> {
    private static final long serialVersionUID = 1L;

    private SubTypedBubbleId<?> subTypedBubbleId;

    @Override
    public SubTypedBubbleEndringId<?> getId() {
        return (SubTypedBubbleEndringId<?>) super.getId();
    }

    @Override
    public SubTypedBubbleId<?> getEndretBubbleId() {
        return getSubTypedBubbleId();
    }

    @Override
    protected void setEndretBubbleIdImpl(BubbleId<?> id) {
        setSubTypedBubbleId((SubTypedBubbleId<?>) id);
    }

    public SubTypedBubbleId<?> getSubTypedBubbleId() {
        return subTypedBubbleId;
    }

    public void setSubTypedBubbleId(SubTypedBubbleId<?> subTypedBubbleId) {
        this.subTypedBubbleId = subTypedBubbleId;
    }
}
