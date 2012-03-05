package no.statkart.skif.storetest.domain.multikobling;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubbleId;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class ServituttId<T extends Servitutt> extends RettsstiftelseId<T> {
    public ServituttId(Long value) {
        super(value);
    }

    public ServituttId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
