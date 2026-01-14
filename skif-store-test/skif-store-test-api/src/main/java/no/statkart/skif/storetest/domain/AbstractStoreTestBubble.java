package no.statkart.skif.storetest.domain;

import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionContext;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public abstract class AbstractStoreTestBubble extends AbstractBubbleObject implements StoreTestBubble {

    public AbstractStoreTestBubble(BubbleId<?> id) {
        super(id);
    }

    public AbstractStoreTestBubble() {
    }

    @Override
    public AbstractStoreTestBubbleId<?> getId() {
        BubbleId<?> id;
        try {
            id = super.getId();
        } catch (ClassCastException e) {
            id = null;
        }
        if (id == null) {
            return null;
        }
        if (id instanceof AbstractStoreTestBubbleId) {
            return (AbstractStoreTestBubbleId<?>) id;
        }
        Object value = id instanceof BubbleId ? ((BubbleId<?>) id).getValue() : id;
        SnapshotVersion snapshotVersion = id instanceof BubbleId
                ? ((BubbleId<?>) id).getSnapshotVersion()
                : SnapshotVersionContext.getInstance().getSnapshotVersion();
        Class<? extends BubbleId<?>> idClass = BubbleIds.getBubbleIdClass((Class) getClass());
        return (AbstractStoreTestBubbleId<?>) BubbleIds.createInstance((Class) idClass, value, snapshotVersion);
    }
}
