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
    protected boolean compatible(AbstractBubbleId id) {
        return id instanceof KodelisteId;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +"{" +
                "value='" + getValue() + '\'' +
                '}';
    }

}