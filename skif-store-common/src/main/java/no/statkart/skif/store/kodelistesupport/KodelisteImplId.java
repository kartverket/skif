package no.statkart.skif.store.kodelistesupport;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.AbstractBubbleId;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class KodelisteImplId<T extends KodelisteImpl> extends AbstractBubbleId<T> implements KodelisteId<T> {

    @Override
    public Long getValue() {
        return (Long)super.getValue();
    }

    public KodelisteImplId(long value) {
        super(new Long(value));
    }

    public KodelisteImplId(Long value) {
        super(value);
    }

    public KodelisteImplId(String value) {
        super(Long.parseLong(value));
    }

    public KodelisteImplId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    public boolean equals(Object id) {
        if (id == null) return false;
        return id instanceof KodelisteImplId && equals((KodelisteImplId) id);
    }

    protected boolean equals(AbstractBubbleId id) {
        return getValue().equals(id.getValue()) && getSnapshotVersion().equals(id.getSnapshotVersion());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +"{" +
                "value='" + getValue() + '\'' +
                '}';
    }

}