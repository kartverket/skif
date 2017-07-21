package no.statkart.skif.storetest.domain.multikobling_old;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class ServituttId<T extends Servitutt> extends RettsstiftelseId<T> {
    @SuppressWarnings("unused")
    public ServituttId(Long value) {
        super(value);
    }

    public ServituttId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
