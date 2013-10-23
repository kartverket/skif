package no.statkart.skif.storetest.domain.multikobling_old;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class
        PengeheftelseId<T extends Pengeheftelse> extends RettsstiftelseId<T> {
    public PengeheftelseId(Long value) {
        super(value);
    }

    public PengeheftelseId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
