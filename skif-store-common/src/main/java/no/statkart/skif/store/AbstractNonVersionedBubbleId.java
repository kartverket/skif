package no.statkart.skif.store;

/**
 * @author Henrik Fredholm
 */
public abstract class AbstractNonVersionedBubbleId<T extends BubbleObject> extends AbstractBubbleId<T> {
    private static final long serialVersionUID = 1L;

    protected AbstractNonVersionedBubbleId() {
    }

    protected AbstractNonVersionedBubbleId(Object value) {
        super(value);
    }

    protected AbstractNonVersionedBubbleId(Object value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion==SnapshotVersion.OLD ? SnapshotVersion.OLD : SnapshotVersion.CURRENT);
    }
}
