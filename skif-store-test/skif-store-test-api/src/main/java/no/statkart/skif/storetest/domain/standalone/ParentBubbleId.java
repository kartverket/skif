package no.statkart.skif.storetest.domain.standalone;

import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

/**
 * @since 2.1
 * @author Jan Holmen
 */
public class ParentBubbleId<T extends ParentBubble> extends AbstractBubbleId<T> implements StoreTestBubbleId<T> {

    @Override
    public Long getValue() {
        return (Long) super.getValue();
    }

    public ParentBubbleId(int idValue) {
        super((long) idValue);
    }

    public ParentBubbleId(Long idValue) {
        super(idValue);
    }

    @SuppressWarnings("unused")
    public ParentBubbleId(Long value, SnapshotVersion version) {
        super(value, version);
    }
}
