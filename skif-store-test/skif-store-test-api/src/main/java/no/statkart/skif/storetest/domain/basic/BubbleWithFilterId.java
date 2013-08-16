package no.statkart.skif.storetest.domain.basic;

import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleId;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;
import no.statkart.skif.storetest.domain.standalone.FilteredBubble;

/**
 * @author Henrik Fredholm
 * @since 2.3
 */
public class BubbleWithFilterId<T extends BubbleWithFilter> extends AbstractStoreTestBubbleId<T> {
    private static final long serialVersionUID = 1L;

    public BubbleWithFilterId(int idValue) {
        super(new Long(idValue));
    }

    public BubbleWithFilterId(Long idValue) {
        super(idValue);
    }

    public BubbleWithFilterId(Long value, SnapshotVersion version) {
        super(value, version);
    }
}
