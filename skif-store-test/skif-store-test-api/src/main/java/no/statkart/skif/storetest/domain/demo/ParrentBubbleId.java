package no.statkart.skif.storetest.domain.demo;

import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

/**
 * @since 2.1
 * @author Jan Holmen
 */
public class ParrentBubbleId<T extends ParrentBubble> extends AbstractBubbleId<T> implements StoreTestBubbleId<T> {

    @Override
    public Long getValue() {
        return (Long) super.getValue();
    }

    public ParrentBubbleId() {
        super();
    }

    public ParrentBubbleId(int idValue) {
        super(new Long(idValue));
    }

    public ParrentBubbleId(Long idValue) {
        super(idValue);
    }

    public ParrentBubbleId(Long value, SnapshotVersion version) {
        super(value, version);
    }
}
