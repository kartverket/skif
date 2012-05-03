package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class HjemmelForPersonId<T extends HjemmelForPerson> extends RettsstiftelseId<T> {
    public HjemmelForPersonId(Long value) {
        super(value);
    }

    public HjemmelForPersonId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
