package no.statkart.skif.store.kodeliste;

import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public abstract class KodeId<T extends Kode> extends AbstractBubbleId<T> {
    protected KodeId(Object value) {
        super(value);
    }

    protected KodeId(Object value, SnapshotVersion version) {
        super(value, version);
    }

    public abstract KodelisteId<?> getKodelisteId();

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "value='" + getValue() + '\'' +
                '}';
    }

}
