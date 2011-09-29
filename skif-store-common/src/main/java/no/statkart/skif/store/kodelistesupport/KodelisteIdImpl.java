package no.statkart.skif.store.kodelistesupport;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.AbstractBubbleId;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class KodelisteIdImpl<T extends KodelisteImpl> extends AbstractBubbleId<T> implements KodelisteId<T> {

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
    public String toString() {
        return getClass().getSimpleName() +"{" +
                "value='" + getValue() + '\'' +
                '}';
    }

}