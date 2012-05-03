package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class HjemmelForMatrikkelenhetId<T extends HjemmelForMatrikkelenhet> extends RettsstiftelseId<T> {
    public HjemmelForMatrikkelenhetId(Long value) {
        super(value);
    }

    public HjemmelForMatrikkelenhetId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
