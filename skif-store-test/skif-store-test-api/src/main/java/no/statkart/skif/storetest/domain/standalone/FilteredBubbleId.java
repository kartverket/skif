package no.statkart.skif.storetest.domain.standalone;

import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

/**
 * @since 2.1
 * @author Jan Holmen
 */
public class FilteredBubbleId<T extends FilteredBubble> extends AbstractBubbleId<T> implements StoreTestBubbleId<T> {

    @Override
    public Long getValue() {
        return (Long) super.getValue();
    }

    public FilteredBubbleId(Long idValue) {
        super(idValue);
    }

    public FilteredBubbleId(Long value, SnapshotVersion version) {
        super(value, version);
    }
}
