package no.statkart.skif.store.kodelistesupport;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.AbstractBubbleId;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class KodelisteImplId<T extends KodelisteImpl> extends AbstractBubbleId<T> implements KodelisteId<T> {
    protected KodelisteImplId() {
    }

    protected KodelisteImplId(Object value) {
        super(value);
    }

    protected KodelisteImplId(Object value, SnapshotVersion version) {
        super(value, version);
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