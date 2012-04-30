package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class
        HjemmelsinformasjonId<T extends Hjemmelsinformasjon> extends RettsstiftelseId<T> {
    public HjemmelsinformasjonId(Long value) {
        super(value);
    }

    public HjemmelsinformasjonId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
