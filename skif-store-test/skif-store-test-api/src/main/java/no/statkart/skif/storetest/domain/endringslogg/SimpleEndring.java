package no.statkart.skif.storetest.domain.endringslogg;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.storetest.domain.basic.SimpleId;

/**
 * Endring for {@link no.statkart.skif.storetest.domain.basic.Simple}.
 *
 * @author Tor Egil R. Strand
 * @since 2.3.0
 */
public class SimpleEndring<I extends SimpleId<?>> extends Endring<I> {
    private static final long serialVersionUID = 1L;

    private SimpleId<?> simpleId;

    @Override
    public SimpleEndringId<?> getId() {
        return (SimpleEndringId<?>) super.getId();
    }

    @Override
    public SimpleId<?> getEndretBubbleId() {
        return getSimpleId();
    }

    @Override
    protected void setEndretBubbleIdImpl(BubbleId<?> id) {
        setSimpleId((SimpleId<?>) id);
    }

    public SimpleId<?> getSimpleId() {
        return simpleId;
    }

    void setSimpleId(SimpleId<?> simpleId) {
        this.simpleId = simpleId;
    }
}
