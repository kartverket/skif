package no.statkart.skif.store.kodelistesupport;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.AbstractBubbleId;
import sun.awt.SunHints;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class KodelisteIdImpl<T extends KodelisteImpl> extends AbstractBubbleId<T> implements KodelisteId<T> {

    @Override
    public Long getValue() {
        return (Long)super.getValue();
    }

    public KodelisteIdImpl(long value) {
        super(new Long(value));
    }

    public KodelisteIdImpl(Long value) {
        super(value);
    }

    public KodelisteIdImpl(String value) {
        super(Long.parseLong(value));
    }

    public KodelisteIdImpl(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    public boolean equals(Object id) {
        if (id == null) return false;
        return id instanceof KodelisteIdImpl && equals((KodelisteIdImpl) id);
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