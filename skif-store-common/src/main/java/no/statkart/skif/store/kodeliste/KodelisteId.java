package no.statkart.skif.store.kodeliste;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.AbstractBubbleId;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class KodelisteId<T extends Kodeliste> extends AbstractBubbleId<T>  {
    protected KodelisteId() {
    }

    protected KodelisteId(Object value) {
        super(value);
    }

    protected KodelisteId(Object value, SnapshotVersion version) {
        super(value, version);
    }


    @Override
    public boolean equals(Object id) {
        if (id == null) return false;
        return id instanceof KodelisteId && equals((KodelisteId) id);
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