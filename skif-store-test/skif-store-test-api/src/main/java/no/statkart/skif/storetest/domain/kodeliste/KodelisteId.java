package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.BubbleKodelisteId;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class KodelisteId<T extends Kodeliste> extends StoreTestBubbleId<T> implements BubbleKodelisteId<T> {

    public KodelisteId(long value) {
        super(new Long(value));
    }

    public KodelisteId(Long value) {
        super(value);
    }

    public KodelisteId(String value) {
        super(Long.parseLong(value));
    }

    public KodelisteId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +"{" +
                "value='" + getValue() + '\'' +
                '}';
    }

}