package no.statkart.skif.storetest2.domain.subtype;

import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest2.domain.StoreTest2BubbleId;

/**
 * Id for {@link SubTypedBubble}.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class SubTypedBubbleId<T extends SubTypedBubble> extends AbstractBubbleId<T> implements StoreTest2BubbleId<T> {
    public SubTypedBubbleId(Long value) {
        super(value);
    }

    public SubTypedBubbleId(Long value, SnapshotVersion version) {
        super(value, version);
    }

    @Override
    public Long getValue() {
        return (Long) super.getValue();
    }
}
