package no.statkart.skif.storetest.domain.demo;

import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

/**
 * @author Roar Ingebrigtsen
 * @since 2.1
 */
public class BubbleWithListId<T extends BubbleWithList> extends AbstractBubbleId<T> implements StoreTestBubbleId<T> {

    public BubbleWithListId(Long value) {
        super(value);
    }

    public BubbleWithListId(Long value, SnapshotVersion version) {
        super(value, version);
    }

    @Override
    public Long getValue() {
        return (Long)super.getValue();
    }

}
