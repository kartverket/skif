package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Knut Inge Bøe
 */
public class RegistreringAnkeOverNektingId<T extends RegistreringAnkeOverNekting> extends RegistreringAnkeId<T> {
    public RegistreringAnkeOverNektingId(Long value) {
        super(value);
    }

    public RegistreringAnkeOverNektingId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static RegistreringAnkeOverNektingId<?> create(long value) {
        return new RegistreringAnkeOverNektingId<RegistreringAnkeOverNekting>(new Long(value));
    }
}
